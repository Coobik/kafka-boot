package com.github.coobik.kprod.kafka;

import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.github.coobik.kprod.kafka.MessageSender.MessageSenderBase;
import com.github.coobik.kprod.model.Message;


@Component
@Profile("simple")
public class SimpleMessageSender extends MessageSenderBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleMessageSender.class);

  @Autowired
  private KafkaProducer<String, Message> streamKafkaProducer;

  @PreDestroy
  public void close() {
    if (streamKafkaProducer != null) {
      LOGGER.info("closing streamKafkaProducer...");
      streamKafkaProducer.close(30L, TimeUnit.SECONDS);
      LOGGER.info("streamKafkaProducer closed");
    }
  }

  @Override
  protected void sendMessageInternal(String key, Message message) {
    ProducerRecord<String, Message> record =
        new ProducerRecord<String, Message>(getTopic(), key, message);

    streamKafkaProducer.send(
        record,
        (metadata, ex) -> {
          logRecordMetadata(metadata);
          logFailure(ex);
        });
  }

  @Override
  protected Logger getLogger() {
    return LOGGER;
  }

}
