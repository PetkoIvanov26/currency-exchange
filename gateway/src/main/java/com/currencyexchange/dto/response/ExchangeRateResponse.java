package com.currencyexchange.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ExchangeRateResponse {
  private CurrencyResponse sourceCurrency;

  private CurrencyResponse targetCurrency;

  private BigDecimal exchangeRate;

  private LocalDateTime timestamp;
}
