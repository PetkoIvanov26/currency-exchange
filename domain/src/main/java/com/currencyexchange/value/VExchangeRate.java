package com.currencyexchange.value;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VExchangeRate {

  private VCurrency sourceCurrency;

  private VCurrency targetCurrency;

  private BigDecimal exchangeRate;

  private LocalDateTime timestamp;
}