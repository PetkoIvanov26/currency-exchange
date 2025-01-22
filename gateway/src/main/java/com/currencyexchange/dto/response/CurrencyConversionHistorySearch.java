package com.currencyexchange.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class CurrencyConversionHistorySearch {
  private Long totalPages;
  private Integer totalItems;
  private Integer currentPage;
  private List<CurrencyConversionHistoryResponse> currencyConversionHistory;
}
