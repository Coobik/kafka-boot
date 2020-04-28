package com.github.coobik.kcons.service;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.coobik.kcons.model.Message;


@Service
public class MessageProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessor.class);

  /**
   * CountDownLatch for testing
   */
  private CountDownLatch latch;

  private final List<Message> processedMessages = new LinkedList<>();

  @PostConstruct
  public void init() {
    LOGGER.info(getClass().getSimpleName() + " started");
  }

  public void processMessage(Message message) {
    if (message == null) {
      LOGGER.warn("empty message received");
      return;
    }

    LOGGER.info("message: {} : {}",
        message.getTimestamp(), message.getValue());

    processedMessages.add(message);

    afterProcessMessage();
  }

  private void afterProcessMessage() {
    if (latch == null) {
      return;
    }

    latch.countDown();
  }

  public List<Message> getProcessedMessages() {
    return processedMessages;
  }

}
