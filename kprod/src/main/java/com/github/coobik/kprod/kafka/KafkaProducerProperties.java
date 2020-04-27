package com.github.coobik.kprod.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "kafka.producer")
public class KafkaProducerProperties {

  private String bootstrapServers;
  private String clientId;
  private String topic;

  private int retries;
  private long retryBackoffMs;

  public String getBootstrapServers() {
    return bootstrapServers;
  }

  public void setBootstrapServers(String bootstrapServers) {
    this.bootstrapServers = bootstrapServers;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }

  public int getRetries() {
    return retries;
  }

  public void setRetries(int retries) {
    this.retries = retries;
  }

  public long getRetryBackoffMs() {
    return retryBackoffMs;
  }

  public void setRetryBackoffMs(long retryBackoffMs) {
    this.retryBackoffMs = retryBackoffMs;
  }

}
