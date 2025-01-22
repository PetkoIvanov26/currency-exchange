package com.currencyexchange.scheduler;

import com.currencyexchange.exception.DomainException;
import com.currencyexchange.service.CurrencyDomainService;
import com.currencyexchange.service.ExchangeRateAPIService;
import com.currencyexchange.service.ExchangeRateDomainService;
import com.currencyexchange.value.VCurrency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
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

  @Scheduled(cron = "0 * * * * *")
  public void updateExchangeRates() {
    try {
      Map<String, BigDecimal> rates = exchangeRateAPIService.fetchExchangeRates();

      VCurrency baseCurrency = currencyDomainService.findOrCreateCurrency("EUR");

      rates.forEach((currencyCode, rate) -> {
        VCurrency targetCurrency = currencyDomainService.findOrCreateCurrency(currencyCode);
        try {
          exchangeRateDomainService.saveOrUpdateExchangeRate(baseCurrency.getId(), targetCurrency.getId(), rate);
        } catch (DomainException e) {
          throw new RuntimeException(e);
        }
      });

      System.out.println("Exchange rates updated successfully.");
    } catch (Exception e) {
      System.err.println("Error fetching exchange rates: " + e.getMessage());
    }
  }


}