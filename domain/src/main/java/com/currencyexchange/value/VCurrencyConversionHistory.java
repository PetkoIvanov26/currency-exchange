package com.currencyexchange.value;

import com.currencyexchange.entity.Currency;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VCurrencyConversionHistory {
  private Long id;

  private Currency sourceCurrency;

  private Currency targetCurrency;

  private BigDecimal amount;

  private BigDecimal convertedAmount;

  private String transactionId;

  private LocalDateTime timestamp;
}
