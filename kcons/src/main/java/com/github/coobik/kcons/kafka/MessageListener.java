package com.github.coobik.kcons.kafka;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.github.coobik.kcons.model.Message;
import com.github.coobik.kcons.service.MessageProcessor;


@Component
public class MessageListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

  @Autowired
  private MessageProcessor messageProcessor;

  @PostConstruct
  public void init() {
    LOGGER.info(getClass().getSimpleName() + " started");
  }

  /**
   * Listen to Kafka topic
   * 
   * @see <a href="https://github.com/spring-projects/spring-kafka/issues/716">spring-kafka</a>
   */
  @KafkaListener(
      id = "messageKafkaListener",
      idIsGroup = false,
      topics = "${kafka.consumer.topic}",
      containerFactory = "messageListenerContainerFactory")
  public void listen(@Payload(required = false) Message message, @Headers MessageHeaders headers) {
    if (message == null) {
      LOGGER.warn("empty message received");
      return;
    }

    if (headers != null) {
      LOGGER.info("headers: {}", headers);
    }

    messageProcessor.processMessage(message);
  }

}
