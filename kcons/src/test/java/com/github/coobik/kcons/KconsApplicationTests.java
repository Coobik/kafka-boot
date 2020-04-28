package com.github.coobik.kcons;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;


@RunWith(JUnitPlatform.class)
@SpringBootTest(
    properties = {
        "kafka.consumer.enabled=false"
    })
public class KconsApplicationTests {

  @Test
  public void contextLoads() {
  }

}
