package com.github.coobik.kstrm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(
    properties = {
        "kafka.streams.enabled=false"
    })
public class KstrmApplicationTests {

  @Test
  public void contextLoads() {
  }

}
