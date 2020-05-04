package com.github.coobik.kstrm.kafka;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnProperty(prefix = "kafka.streams", name = "enabled", havingValue = "true")
public class KafkaStreamsConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsConfiguration.class);

  @Autowired
  private KafkaStreamsProperties kafkaStreamsProperties;

  @Bean
  @Autowired
  public KafkaStreams kafkaStreams(
      Topology topology,
      @Qualifier("streamProperties") Properties streamProperties) {
    return new KafkaStreams(topology, streamProperties);
  }

  @Bean
  public Topology topology() {
    StreamsBuilder streamsBuilder = new StreamsBuilder();

    KStream<String, String> messageStream =
        streamsBuilder.stream(kafkaStreamsProperties.getInputTopic());

    defineStreamProcessing(messageStream);

    Topology topology = streamsBuilder.build();
    LOGGER.info("kafka streams {}", topology.describe());

    return topology;
  }

  private void defineStreamProcessing(KStream<String, String> messageStream) {
    KStream<String, String> mappedStream =
        messageStream
            .filterNot((key, value) -> StringUtils.isBlank(value))
            .filter((key, value) -> value.contains(kafkaStreamsProperties.getValueFilter()))
            .mapValues((key, value) -> String.format("{\"%s\":%s}", key, value));

    mappedStream.to(kafkaStreamsProperties.getOutputTopic());

    KTable<String, Long> countTable =
        mappedStream
            .groupByKey()
            .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("count-store"));

    LOGGER.info("countTable store: {}", countTable.queryableStoreName());

    countTable
        .toStream()
        .to(kafkaStreamsProperties.getCountOutputTopic(),
            Produced.with(Serdes.String(), Serdes.Long()));
  }

  @Bean("streamProperties")
  public Properties streamProperties() {
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
