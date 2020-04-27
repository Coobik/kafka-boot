package com.github.coobik.kprod.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.github.coobik.kprod.model.Message;


@Configuration
public class KafkaProducerConfiguration {

  // TODO: kafka producer properties bean
  @Value("${kafka.bootstrap.servers}")
  private String kafkaBootstrapServers;

  @Value("${kafka.producer.clientId}")
  private String clientId;

  // TODO: spring boot kafka template

  @Autowired
  @Bean("streamKafkaProducer")
  public KafkaProducer<String, Message> streamKafkaProducer(
      @Qualifier("streamKafkaProducerProperties") Properties streamKafkaProducerProperties) {
    return new KafkaProducer<String, Message>(streamKafkaProducerProperties);
  }

  @Bean("streamKafkaProducerProperties")
  public Properties streamKafkaProducerProperties() {
    Properties properties = new Properties();

    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
    properties.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
    properties.put(ProducerConfig.ACKS_CONFIG, "all");
    properties.put(ProducerConfig.RETRIES_CONFIG, 3);
    properties.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
    properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 0);
    properties.put(ProducerConfig.LINGER_MS_CONFIG, 0);
    // properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432); // 32 mb
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

    return properties;
  }

}
