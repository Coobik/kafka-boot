package com.github.coobik.kcons.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialRandomBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.github.coobik.kcons.model.Message;


@Configuration
@EnableKafka
public class KafkaConsumerConfiguration {

  @Autowired
  private KafkaConsumerProperties kafkaConsumerProperties;

  @Autowired
  @Bean("messageListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, Message> messageListenerContainerFactory(
      RetryTemplate retryTemplate) {
    ConcurrentKafkaListenerContainerFactory<String, Message> containerFactory =
        new ConcurrentKafkaListenerContainerFactory<>();

    containerFactory.setConsumerFactory(messageConsumerFactory());
    containerFactory.setRetryTemplate(retryTemplate);

    containerFactory.setAutoStartup(true);
    containerFactory.setBatchListener(false);

    return containerFactory;
  }

  private ConsumerFactory<String, Message> messageConsumerFactory() {
    DefaultKafkaConsumerFactory<String, Message> messageConsumerFactory =
        new DefaultKafkaConsumerFactory<>(consumerConfig());

    messageConsumerFactory.setKeyDeserializer(new StringDeserializer());
    messageConsumerFactory.setValueDeserializer(messageDeserializer());

    return messageConsumerFactory;
  }

  private Deserializer<Message> messageDeserializer() {
    NullableJsonDeserializer<Message> messageDeserializer =
        new NullableJsonDeserializer<>(
            Message.class,
            kafkaConsumerProperties.isUseHeadersIfPresent());

    String[] trustedPackages = kafkaConsumerProperties.getTrustedPackages();

    if (ArrayUtils.isNotEmpty(trustedPackages)) {
      messageDeserializer.addTrustedPackages(trustedPackages);
    }

    return messageDeserializer;
  }

  private Map<String, Object> consumerConfig() {
    Map<String, Object> consumerConfigs = new HashMap<>();

    consumerConfigs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
        kafkaConsumerProperties.getBootstrapServers());

    consumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG,
        kafkaConsumerProperties.getGroupId());

    consumerConfigs.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG,
        kafkaConsumerProperties.getMaxPollIntervalMs());

    consumerConfigs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
        kafkaConsumerProperties.getAutoOffsetReset());

    return consumerConfigs;
  }

  @Bean
  public RetryTemplate retryTemplate() {
    RetryTemplate retryTemplate = new RetryTemplate();

    retryTemplate.setRetryPolicy(retryPolicy());
    retryTemplate.setBackOffPolicy(backOffPolicy());

    return retryTemplate;
  }

  private RetryPolicy retryPolicy() {
    SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();

    retryPolicy.setMaxAttempts(kafkaConsumerProperties.getMaxAttempts());

    return retryPolicy;
  }

  private BackOffPolicy backOffPolicy() {
    ExponentialRandomBackOffPolicy backOffPolicy = new ExponentialRandomBackOffPolicy();

    backOffPolicy.setInitialInterval(kafkaConsumerProperties.getInitialInterval());
    backOffPolicy.setMultiplier(kafkaConsumerProperties.getMultiplier());

    return backOffPolicy;
  }

}
