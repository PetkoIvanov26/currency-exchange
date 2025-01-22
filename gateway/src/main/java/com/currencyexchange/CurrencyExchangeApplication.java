package com.currencyexchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.currencyexchange", "domain.com.currencyexchange"})
@EntityScan(basePackages = "com.currencyexchange.entity")
public class CurrencyExchangeApplication {

  public static void main(String[] args) {
    SpringApplication.run(CurrencyExchangeApplication.class, args);
  }

}