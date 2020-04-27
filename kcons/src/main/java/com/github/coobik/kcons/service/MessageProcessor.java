package com.github.coobik.kcons.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.coobik.kcons.model.Message;


@Service
public class MessageProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessor.class);

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
  }

}
