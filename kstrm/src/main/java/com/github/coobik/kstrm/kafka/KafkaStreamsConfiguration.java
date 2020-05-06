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
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.SessionWindowedKStream;
import org.apache.kafka.streams.kstream.SessionWindows;
import org.apache.kafka.streams.kstream.TimeWindowedKStream;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.SessionStore;
import org.apache.kafka.streams.state.WindowStore;
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
    KStream<String, String> mappedStream = mapStream(messageStream);
    KGroupedStream<String, String> groupedStream = mappedStream.groupByKey();
    countStream(groupedStream);
    countWindowedStream(groupedStream);
    countSessionStream(groupedStream);
  }

  private void countWindowedStream(KGroupedStream<String, String> groupedStream) {
    TimeWindows windows =
        TimeWindows
            .of(10000L)
            .advanceBy(10000L)
            .until(12000L);

    TimeWindowedKStream<String, String> windowedStream = groupedStream.windowedBy(windows);

    KTable<Windowed<String>, Long> windowedCountTable =
        windowedStream.count(
            Materialized.<String, Long, WindowStore<Bytes, byte[]>>as("windowed-count-store"));

    // TODO: no suppress in kafka-streams 2.0.1

    LOGGER.info("windowedCountTable store: {}", windowedCountTable.queryableStoreName());

    windowedTableToTopic(windowedCountTable, kafkaStreamsProperties.getWindowedCountOutputTopic());
  }

  private void countSessionStream(KGroupedStream<String, String> groupedStream) {
    long inactivityGapMs = 30000L;

    SessionWindows windows =
        SessionWindows
            .with(inactivityGapMs)
            .until(30000L);

    SessionWindowedKStream<String, String> sessionStream = groupedStream.windowedBy(windows);

    KTable<Windowed<String>, Long> sessionCountTable =
        sessionStream.count(
            Materialized.<String, Long, SessionStore<Bytes, byte[]>>as("session-count-store"));

    LOGGER.info("sessionCountTable store: {}", sessionCountTable.queryableStoreName());

    windowedTableToTopic(sessionCountTable, kafkaStreamsProperties.getSessionCountOutputTopic());
  }

  private void windowedTableToTopic(
      KTable<Windowed<String>, Long> windowedTable,
      String outputTopic) {
    windowedTable
        .toStream((windowedKey, value) -> (windowedKey.key() + "_" + windowedKey.window().end()))
        .to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()));
  }

  private void countStream(KGroupedStream<String, String> groupedStream) {
    KTable<String, Long> countTable =
        groupedStream.count(
            Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("count-store"));

    LOGGER.info("countTable store: {}", countTable.queryableStoreName());

    countTable
        .toStream()
        .to(kafkaStreamsProperties.getCountOutputTopic(),
            Produced.with(Serdes.String(), Serdes.Long()));
  }

  private KStream<String, String> mapStream(KStream<String, String> messageStream) {
    KStream<String, String> mappedStream =
        messageStream
            .filterNot((key, value) -> StringUtils.isBlank(value))
            .filter((key, value) -> value.contains(kafkaStreamsProperties.getValueFilter()))
            .mapValues((key, value) -> String.format("{\"%s\":%s}", key, value));

    mappedStream.to(kafkaStreamsProperties.getOutputTopic());

    return mappedStream;
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

    // local state dir for state storage powered by rocksdb
    streamProperties.put(StreamsConfig.STATE_DIR_CONFIG,
        kafkaStreamsProperties.getStateDir());

    streamProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
        kafkaStreamsProperties.getAutoOffsetReset());

    return streamProperties;
  }

}
