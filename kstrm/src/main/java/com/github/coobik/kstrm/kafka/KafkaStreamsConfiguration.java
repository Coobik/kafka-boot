package com.github.coobik.kstrm.kafka;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnProperty(prefix = "kafka.streams", name = "enabled", havingValue = "true")
public class KafkaStreamsConfiguration {

  @Autowired
  private KafkaStreamsProperties kafkaStreamsProperties;

  @Bean
  public KafkaStreams kafkaStreams() {
    StreamsBuilder streamsBuilder = new StreamsBuilder();

    KStream<String, String> messageStream =
        streamsBuilder.stream(kafkaStreamsProperties.getInputTopic());

    messageStream
        .filter((key, value) -> value.contains("0"))
        .mapValues((key, value) -> String.format("{\"%s\":%s}", key, value))
        .to(kafkaStreamsProperties.getOutputTopic());

    Topology topology = streamsBuilder.build();
    return new KafkaStreams(topology, streamProperties());
  }

  private Properties streamProperties() {
    Properties streamProperties = new Properties();

    streamProperties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
        kafkaStreamsProperties.getBootstrapServers());

    streamProperties.put(StreamsConfig.APPLICATION_ID_CONFIG,
        kafkaStreamsProperties.getApplicationId());

    streamProperties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,
        Serdes.String().getClass());

    // TODO: use json
    streamProperties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,
        Serdes.String().getClass());

    streamProperties.put(StreamsConfig.STATE_DIR_CONFIG,
        kafkaStreamsProperties.getStateDir());

    streamProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
        kafkaStreamsProperties.getAutoOffsetReset());

    return streamProperties;
  }

}
