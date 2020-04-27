package com.github.coobik.kprod.kafka;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.coobik.kprod.model.Message;


public interface MessageSender {

  void sendMessage(String key, Message message);

  public static abstract class MessageSenderBase implements MessageSender {

    @Autowired
    private KafkaProducerProperties kafkaProducerProperties;

    @Autowired
    private ObjectMapper objectMapper;

    protected abstract Logger getLogger();

    protected abstract void sendMessageInternal(String key, Message message);

    @PostConstruct
    public void init() {
      getLogger().info(getClass().getSimpleName() + " started");
    }

    @Override
    public void sendMessage(String key, Message message) {
      if (message == null) {
        return;
      }

      String json = toJson(message);
      getLogger().info("sending message: {} : {} to: {}", key, json, getTopic());

      sendMessageInternal(key, message);
    }

    protected String toJson(Message message) {
      try {
        return objectMapper.writeValueAsString(message);
      }
      catch (JsonProcessingException ex) {
        getLogger().error("error writing to json", ex);
        return "";
      }
    }

    protected String getTopic() {
      return kafkaProducerProperties.getTopic();
    }

    protected void logRecordMetadata(RecordMetadata metadata) {
      if (metadata == null) {
        return;
      }

      getLogger().info("complete sending message to: {} partition: {} offset: {}",
          metadata.topic(), metadata.partition(), metadata.offset());
    }

    protected void logFailure(Throwable ex) {
      if (ex == null) {
        return;
      }

      getLogger().error("error sending message", ex);
    }

  }

}
