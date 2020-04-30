package com.github.coobik.kstrm.kafka;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.streams.KafkaStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;


@Component
@ConditionalOnBean(KafkaStreamsConfiguration.class)
public class KafkaStreamsStarter {

  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsStarter.class);

  @Autowired
  private KafkaStreams kafkaStreams;

  @PostConstruct
  public void init() {
    LOGGER.info("{} constructed", getClass().getSimpleName());

    LOGGER.info("starting KafkaStreams...");
    kafkaStreams.start();
    LOGGER.info("KafkaStreams started");
  }

  @PreDestroy
  public void close() {
    LOGGER.info("closing KafkaStreams...");
    kafkaStreams.close();
    LOGGER.info("KafkaStreams closed");
  }

}
