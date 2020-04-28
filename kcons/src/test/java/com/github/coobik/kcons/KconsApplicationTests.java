package com.github.coobik.kcons;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(
    properties = {
        "kafka.consumer.enabled=false"
    })
public class KconsApplicationTests {

  @Test
  public void contextLoads() {
  }

}
