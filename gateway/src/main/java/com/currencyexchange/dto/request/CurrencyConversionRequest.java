package com.currencyexchange.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CurrencyConversionRequest {
  private BigDecimal amount;
  @Size(min = 3, max = 3, message = "Source currency code must be exactly 3 characters.")
  private String sourceCurrencyCode;
  @Size(min = 3, max = 3, message = "Target currency code must be exactly 3 characters.")
  private String targetCurrencyCode;
}
