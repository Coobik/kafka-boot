package com.github.coobik.kprod.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.github.coobik.kprod.model.Message;


@Configuration
public class KafkaProducerConfiguration {

  @Autowired
  private KafkaProducerProperties kafkaProducerProperties;

  // TODO: spring boot kafka template

  @Profile("simple")
  @Bean("streamKafkaProducer")
  public KafkaProducer<String, Message> streamKafkaProducer() {
    return new KafkaProducer<String, Message>(streamKafkaProducerConfig());
  }

  @Profile("template")
  @Bean("streamKafkaTemplate")
  public KafkaTemplate<String, Message> streamKafkaTemplate() {
    return new KafkaTemplate<String, Message>(producerFactory());
  }

  private ProducerFactory<String, Message> producerFactory() {
    return new DefaultKafkaProducerFactory<>(streamKafkaProducerConfig());
  }

  /**
   * config map for kafka producer. do not make this map a Bean because spring wraps it with another
   * map
   * 
   * @see <a href=
   *      "https://stackoverflow.com/questions/41056765/spring-java-config-wraps-injected-map">stackoverflow</a>
   */
  private Map<String, Object> streamKafkaProducerConfig() {
    Map<String, Object> config = new HashMap<>();

    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
        kafkaProducerProperties.getBootstrapServers());

    config.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaProducerProperties.getClientId());
    config.put(ProducerConfig.ACKS_CONFIG, "all");
    config.put(ProducerConfig.RETRIES_CONFIG, kafkaProducerProperties.getRetries());
    config.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, kafkaProducerProperties.getRetryBackoffMs());
    config.put(ProducerConfig.BATCH_SIZE_CONFIG, 0);
    config.put(ProducerConfig.LINGER_MS_CONFIG, 0);
    // config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432); // 32 mb
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

    return config;
  }

}
