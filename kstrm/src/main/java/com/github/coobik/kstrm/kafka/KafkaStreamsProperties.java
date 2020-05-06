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
  private String countOutputTopic;
  private String windowedCountOutputTopic;
  private String sessionCountOutputTopic;

  private String autoOffsetReset;

  private String valueFilter;

  private boolean cleanUpBeforeStart;
  private boolean cleanUpAfterClose;

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

  public String getCountOutputTopic() {
    return countOutputTopic;
  }

  public void setCountOutputTopic(String countOutputTopic) {
    this.countOutputTopic = countOutputTopic;
  }

  public String getWindowedCountOutputTopic() {
    return windowedCountOutputTopic;
  }

  public void setWindowedCountOutputTopic(String windowedCountOutputTopic) {
    this.windowedCountOutputTopic = windowedCountOutputTopic;
  }

  public String getSessionCountOutputTopic() {
    return sessionCountOutputTopic;
  }

  public void setSessionCountOutputTopic(String sessionCountOutputTopic) {
    this.sessionCountOutputTopic = sessionCountOutputTopic;
  }

  public String getAutoOffsetReset() {
    return autoOffsetReset;
  }

  public void setAutoOffsetReset(String autoOffsetReset) {
    this.autoOffsetReset = autoOffsetReset;
  }

  public String getValueFilter() {
    return valueFilter;
  }

  public void setValueFilter(String valueFilter) {
    this.valueFilter = valueFilter;
  }

  public boolean isCleanUpBeforeStart() {
    return cleanUpBeforeStart;
  }

  public void setCleanUpBeforeStart(boolean cleanUpBeforeStart) {
    this.cleanUpBeforeStart = cleanUpBeforeStart;
  }

  public boolean isCleanUpAfterClose() {
    return cleanUpAfterClose;
  }

  public void setCleanUpAfterClose(boolean cleanUpAfterClose) {
    this.cleanUpAfterClose = cleanUpAfterClose;
  }

}
