package com.github.coobik.kcons.kafka;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.util.ReflectionTestUtils;


public abstract class TestUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);

  private TestUtils() {
    // static class
  }

  public static <T> KafkaTemplate<String, T> createKafkaTemplate(
      EmbeddedKafkaBroker embeddedKafka, String defaultTopic) {
    Map<String, Object> config = KafkaTestUtils.producerProps(embeddedKafka);
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

    ProducerFactory<String, T> producerFactory = new DefaultKafkaProducerFactory<>(config);
    KafkaTemplate<String, T> kafkaTemplate = new KafkaTemplate<>(producerFactory);

    if (StringUtils.isNotBlank(defaultTopic)) {
      kafkaTemplate.setDefaultTopic(defaultTopic);
    }

    return kafkaTemplate;
  }

  public static void waitForListenerContainers(
      KafkaListenerEndpointRegistry kafkaRegistry,
      EmbeddedKafkaBroker embeddedKafka) {
    kafkaRegistry
        .getListenerContainers()
        .stream()
        .filter(container -> container instanceof ConcurrentMessageListenerContainer)
        .forEach(container -> {
          ContainerTestUtils.waitForAssignment(container, embeddedKafka.getPartitionsPerTopic());

          LOGGER.info("listener container {}:{} ready",
              container.getGroupId(), container.getListenerId());
        });
  }

  @SuppressWarnings("unchecked")
  public static <T> T getProxyTarget(T object) throws Exception {
    if ((AopUtils.isAopProxy(object)) && (object instanceof Advised)) {
      Advised advised = (Advised) object;
      return (T) advised.getTargetSource().getTarget();
    }

    return object;
  }

  public static <T> CountDownLatch setCountDownLatchOnTarget(T target) throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    T realTarget = getProxyTarget(target);
    ReflectionTestUtils.setField(realTarget, "latch", latch);

    return latch;
  }

  public static void waitOnLatch(CountDownLatch latch, long timeoutMillis)
      throws InterruptedException {
    if (latch == null) {
      return;
    }

    LOGGER.info("waiting on latch for {} millis", timeoutMillis);
    long startTimeMillis = System.currentTimeMillis();

    if (latch.await(timeoutMillis, TimeUnit.MILLISECONDS)) {
      LOGGER.info("waited on latch for {} millis", (System.currentTimeMillis() - startTimeMillis));
      return;
    }

    throw new RuntimeException("timeout waiting on latch");
  }

}
