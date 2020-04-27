package com.github.coobik.kcons.kafka;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.lang.Nullable;


public class NullableJsonDeserializer<T> extends JsonDeserializer<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(NullableJsonDeserializer.class);

  public NullableJsonDeserializer(Class<? super T> targetType, boolean useHeadersIfPresent) {
    super(targetType, useHeadersIfPresent);
  }

  @Override
  public T deserialize(String topic, Headers headers, byte[] data) {
    try {
      return super.deserialize(topic, headers, data);
    }
    catch (SerializationException ex) {
      LOGGER.error("error deserializing message from: {} : {}", topic, ex.getMessage());
      return null;
    }
  }

  @Override
  public T deserialize(String topic, @Nullable byte[] data) {
    try {
      return super.deserialize(topic, data);
    }
    catch (SerializationException ex) {
      LOGGER.error("error deserializing message from: {} : {}", topic, ex.getMessage());
      return null;
    }
  }

}
