package com.github.coobik.kprod.service;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.github.coobik.kprod.kafka.MessageSender;
import com.github.coobik.kprod.model.Message;


@Service
@ConditionalOnBean(MessageSender.class)
public class TelemetryReporter {

  private static final Logger LOGGER = LoggerFactory.getLogger(TelemetryReporter.class);

  @Value("${report.value.length}")
  private int valueLength;

  @Value("${kafka.producer.clientId}")
  private String key;

  @Autowired
  private MessageSender messageSender;

  @PostConstruct
  public void init() {
    LOGGER.info("TelemetryReporter started using {}", messageSender.getClass().getSimpleName());
  }

  @Scheduled(
      fixedDelayString = "${report.fixed.delay.ms}",
      initialDelayString = "${report.initial.delay.ms}")
  public void report() {
    Message message = buildMessage();
    messageSender.sendMessage(key, message);
  }

  private Message buildMessage() {
    Message message = new Message();

    message.setTimestamp(System.currentTimeMillis());
    message.setUnit("qubit");

    String value = RandomStringUtils.randomAlphanumeric(valueLength);
    message.setValue(value);

    return message;
  }

}
