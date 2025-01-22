package com.currencyexchange.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CurrencyConversionHistorySearchRequest {
  @Size(min = 3, max = 3, message = "Source currency code must be exactly 3 characters.")
  private String sourceCurrencyCode;
  @Size(min = 3, max = 3, message = "Target currency code must be exactly 3 characters.")
  private String targetCurrencyCode;
  private String transactionId;
  private LocalDateTime fromDate;
  private LocalDateTime toDate;
  @Min(value = 1, message = "Page must be greater than or equal to 1")
  private Integer page;
  @Min(value = 1, message = "Amount must be greater than or equal to 1")
  private Integer records;
}
