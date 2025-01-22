package com.currencyexchange.value.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VCurrencyConversionHistoryRequest {
  private Long sourceCurrencyId;

  private Long targetCurrencyId;

  private BigDecimal amount;

  private BigDecimal convertedAmount;
}
