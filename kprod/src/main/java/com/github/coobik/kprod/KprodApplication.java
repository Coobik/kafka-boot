package com.github.coobik.kprod;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class KprodApplication {

  public static void main(String[] args) {
    SpringApplication.run(KprodApplication.class, args);
  }

}
