package com.currencyexchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = "com.currencyexchange.entity")
public class ExchangeRateSchedulerApplication {
  public static void main(String[] args) {
    SpringApplication.run(ExchangeRateSchedulerApplication.class, args);
  }
}
