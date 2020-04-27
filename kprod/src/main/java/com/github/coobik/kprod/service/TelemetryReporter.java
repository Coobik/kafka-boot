package com.github.coobik.kprod.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.github.coobik.kprod.kafka.SimpleMessageSender;
import com.github.coobik.kprod.model.Message;


@Service
public class TelemetryReporter {

  @Value("${report.value.length}")
  private int valueLength;

  @Value("${kafka.producer.clientId}")
  private String key;

  @Autowired
  private SimpleMessageSender messageSender;

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
