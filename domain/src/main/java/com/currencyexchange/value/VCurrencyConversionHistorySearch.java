package com.currencyexchange.value;

import lombok.Data;

import java.util.List;

@Data
public class VCurrencyConversionHistorySearch {
  private Long totalPages;
  private Integer totalItems;
  private Integer currentPage;
  private List<VCurrencyConversionHistory> currencyConversionHistory;
}
