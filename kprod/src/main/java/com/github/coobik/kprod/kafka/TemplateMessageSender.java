package com.github.coobik.kprod.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import com.github.coobik.kprod.kafka.MessageSender.MessageSenderBase;
import com.github.coobik.kprod.model.Message;


@Component
@Profile("template")
public class TemplateMessageSender extends MessageSenderBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(TemplateMessageSender.class);

  @Autowired
  private KafkaTemplate<String, Message> streamKafkaTemplate;

  @Override
  protected void sendMessageInternal(String key, Message message) {
    ListenableFuture<SendResult<String, Message>> sendFuture =
        streamKafkaTemplate.send(getTopic(), key, message);

    sendFuture.addCallback(
        result -> logRecordMetadata(result.getRecordMetadata()),
        ex -> logFailure(ex));
  }

  @Override
  protected Logger getLogger() {
    return LOGGER;
  }

}
