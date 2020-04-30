package com.github.coobik.kstrm.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "kafka.streams")
public class KafkaStreamsProperties {

  private boolean enabled;

  private String bootstrapServers;
  private String applicationId;

  private String stateDir;

  private String inputTopic;
  private String outputTopic;

  private String autoOffsetReset;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getBootstrapServers() {
    return bootstrapServers;
  }

  public void setBootstrapServers(String bootstrapServers) {
    this.bootstrapServers = bootstrapServers;
  }

  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  public String getStateDir() {
    return stateDir;
  }

  public void setStateDir(String stateDir) {
    this.stateDir = stateDir;
  }

  public String getInputTopic() {
    return inputTopic;
  }

  public void setInputTopic(String inputTopic) {
    this.inputTopic = inputTopic;
  }

  public String getOutputTopic() {
    return outputTopic;
  }

  public void setOutputTopic(String outputTopic) {
    this.outputTopic = outputTopic;
  }

  public String getAutoOffsetReset() {
    return autoOffsetReset;
  }

  public void setAutoOffsetReset(String autoOffsetReset) {
    this.autoOffsetReset = autoOffsetReset;
  }

}
