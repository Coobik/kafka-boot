package com.github.coobik.kcons.kafka;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import com.github.coobik.kcons.model.Message;
import com.github.coobik.kcons.service.MessageProcessor;


@RunWith(JUnitPlatform.class)
@SpringBootTest(
    properties = {
        // set bootstrapServers to embedded kafka before listeners start
        "kafka.consumer.bootstrapServers=${spring.embedded.kafka.brokers}",
        "kafka.consumer.enabled=true",
        "kafka.consumer.useHeadersIfPresent=true"
    })
@DirtiesContext
@EmbeddedKafka(
    partitions = 1,
    topics = {
        "${kafka.consumer.topic:friday-stream}"
    })
public class MessageListenerTest {

  @Autowired
  private EmbeddedKafkaBroker embeddedKafka;

  @Autowired
  private KafkaListenerEndpointRegistry kafkaRegistry;

  @Autowired
  @SpyBean
  private MessageProcessor messageProcessor;

  @Captor
  private ArgumentCaptor<Message> messageCaptor;

  @Value("${spring.embedded.kafka.brokers}")
  private String kafkaBrokers;

  @Value("${kafka.consumer.topic}")
  private String topic;

  @BeforeEach
  public void beforeEach() {
    MockitoAnnotations.initMocks(this);

    TestUtils.waitForListenerContainers(kafkaRegistry, embeddedKafka);
  }

  @Test
  public void testListenForMessage() throws Exception {
    KafkaTemplate<String, Message> kafkaTemplate =
        TestUtils.createKafkaTemplate(embeddedKafka, topic);

    CountDownLatch latch = TestUtils.setCountDownLatchOnTarget(messageProcessor);

    Message message = buildMessage();
    kafkaTemplate.sendDefault(message);

    TestUtils.waitOnLatch(latch, 10000L);

    checkProcessedMessages(message);
    verifyProcessMessage(message);
  }

  private void verifyProcessMessage(Message message) {
    Mockito.verify(messageProcessor).processMessage(messageCaptor.capture());
    checkMessage(message, messageCaptor.getValue());
  }

  private void checkProcessedMessages(Message expectedMessage) {
    List<Message> processedMessages = messageProcessor.getProcessedMessages();
    Assert.assertEquals(1, processedMessages.size());

    Message actualMessage = processedMessages.get(0);
    checkMessage(expectedMessage, actualMessage);
  }

  private void checkMessage(Message expectedMessage, Message actualMessage) {
    Assert.assertEquals(expectedMessage.getUnit(), actualMessage.getUnit());
    Assert.assertEquals(expectedMessage.getValue(), actualMessage.getValue());
    Assert.assertEquals(expectedMessage.getTimestamp(), actualMessage.getTimestamp());
  }

  private Message buildMessage() {
    Message message = new Message();

    message.setTimestamp(System.currentTimeMillis());
    message.setUnit(RandomStringUtils.randomAlphanumeric(4));
    message.setValue(RandomStringUtils.randomAlphanumeric(8));

    return message;
  }

}
