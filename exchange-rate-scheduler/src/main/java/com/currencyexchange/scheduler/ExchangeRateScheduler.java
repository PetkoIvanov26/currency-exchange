package com.currencyexchange.scheduler;

import com.currencyexchange.exception.DomainException;
import com.currencyexchange.service.CurrencyDomainService;
import com.currencyexchange.service.ExchangeRateAPIService;
import com.currencyexchange.service.ExchangeRateDomainService;
import com.currencyexchange.value.VCurrency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Slf4j
public class ExchangeRateScheduler {
  private final ExchangeRateAPIService exchangeRateAPIService;
  private final ExchangeRateDomainService exchangeRateDomainService;
  private final CurrencyDomainService currencyDomainService;
  @Autowired
  public ExchangeRateScheduler(
    ExchangeRateAPIService exchangeRateAPIService, ExchangeRateDomainService exchangeRateDomainService,
    CurrencyDomainService currencyDomainService) {
    this.exchangeRateAPIService = exchangeRateAPIService;
    this.exchangeRateDomainService = exchangeRateDomainService;
    this.currencyDomainService = currencyDomainService;
  }

  @Scheduled(cron = "${scheduler.cron}")
  public void updateExchangeRates() {
    try {
      Map<String, BigDecimal> rates = exchangeRateAPIService.fetchExchangeRates();

      VCurrency baseCurrency = currencyDomainService.findOrCreateCurrency("EUR");

      rates.forEach((currencyCode, rate) -> {
        VCurrency targetCurrency = currencyDomainService.findOrCreateCurrency(currencyCode);
        try {
          exchangeRateDomainService.saveOrUpdateExchangeRate(baseCurrency.getId(), targetCurrency.getId(), rate);
        } catch (DomainException e) {
          log.error("Creating or updating exchange rate failed", e);
        }
      });

     log.info("Exchange rates updated successfully");
    } catch (Exception e) {
      log.error("Error fetching exchange rates: " + e.getMessage());
    }
  }


}