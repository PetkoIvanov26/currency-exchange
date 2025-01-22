package com.currencyexchange.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CurrencyConversionHistoryResponse {
  private Long id;

  private CurrencyResponse sourceCurrency;

  private CurrencyResponse targetCurrency;

  private BigDecimal amount;

  private BigDecimal convertedAmount;

  private String transactionId;

  private LocalDateTime timestamp;
}
