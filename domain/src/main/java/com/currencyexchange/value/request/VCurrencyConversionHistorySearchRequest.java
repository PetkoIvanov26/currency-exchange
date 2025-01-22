package com.currencyexchange.value.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VCurrencyConversionHistorySearchRequest {
  private String sourceCurrencyCode;
  private String targetCurrencyCode;
  private String transactionId;
  private LocalDateTime fromDate;
  private LocalDateTime toDate;
  private Integer page;
  private Integer records;
}
