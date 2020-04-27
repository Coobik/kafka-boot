package com.github.coobik.kcons.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "kafka.consumer")
public class KafkaConsumerProperties {

  private String bootstrapServers;
  private String groupId;
  private String topic;

  private int maxPollIntervalMs;
  private String autoOffsetReset;

  private int maxAttempts;
  private long initialInterval;
  private double multiplier;

  private boolean useHeadersIfPresent;
  private String[] trustedPackages;

  public String getBootstrapServers() {
    return bootstrapServers;
  }

  public void setBootstrapServers(String bootstrapServers) {
    this.bootstrapServers = bootstrapServers;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }

  public int getMaxPollIntervalMs() {
    return maxPollIntervalMs;
  }

  public void setMaxPollIntervalMs(int maxPollIntervalMs) {
    this.maxPollIntervalMs = maxPollIntervalMs;
  }

  public String getAutoOffsetReset() {
    return autoOffsetReset;
  }

  public void setAutoOffsetReset(String autoOffsetReset) {
    this.autoOffsetReset = autoOffsetReset;
  }

  public int getMaxAttempts() {
    return maxAttempts;
  }

  public void setMaxAttempts(int maxAttempts) {
    this.maxAttempts = maxAttempts;
  }

  public long getInitialInterval() {
    return initialInterval;
  }

  public void setInitialInterval(long initialInterval) {
    this.initialInterval = initialInterval;
  }

  public double getMultiplier() {
    return multiplier;
  }

  public void setMultiplier(double multiplier) {
    this.multiplier = multiplier;
  }

  public boolean isUseHeadersIfPresent() {
    return useHeadersIfPresent;
  }

  public void setUseHeadersIfPresent(boolean useHeadersIfPresent) {
    this.useHeadersIfPresent = useHeadersIfPresent;
  }

  public String[] getTrustedPackages() {
    return trustedPackages;
  }

  public void setTrustedPackages(String[] trustedPackages) {
    this.trustedPackages = trustedPackages;
  }

}
