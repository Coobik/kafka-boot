package com.github.coobik.kprod.kafka;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.coobik.kprod.model.Message;


@Service
public class MessageSender {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageSender.class);

  @Value("${kafka.producer.topic}")
  private String topic;

  @Autowired
  private KafkaProducer<String, Message> streamKafkaProducer;

  @Autowired
  private ObjectMapper objectMapper;

  @PostConstruct
  public void init() {
    LOGGER.info("MessageSender started");
  }

  @PreDestroy
  public void close() {
    if (streamKafkaProducer != null) {
      LOGGER.info("closing streamKafkaProducer...");
      streamKafkaProducer.close(30L, TimeUnit.SECONDS);
      LOGGER.info("streamKafkaProducer closed");
    }
  }

  public void sendMessage(String key, Message message) {
    if (message == null) {
      return;
    }

    String json = toJson(message);
    LOGGER.info("sending message: {} : {} to: {}", key, json, topic);

    ProducerRecord<String, Message> record =
        new ProducerRecord<String, Message>(topic, key, message);

    streamKafkaProducer.send(record, MessageSender::onCompletion);
  }

  private String toJson(Message message) {
    try {
      return objectMapper.writeValueAsString(message);
    }
    catch (JsonProcessingException ex) {
      LOGGER.error("error writing to json", ex);
      return "";
    }
  }

  private static void onCompletion(RecordMetadata metadata, Exception exception) {
    if (metadata != null) {
      LOGGER.info("complete sending message to: {} partition: {} offset: {}",
          metadata.topic(), metadata.partition(), metadata.offset());
    }

    if (exception != null) {
      LOGGER.error(
          "exception while sending message to: {} : {}",
          ((metadata == null) ? ("") : (metadata.topic())),
          exception.getMessage());
    }
  }

}
