package com.currencyexchange.value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VCurrencyConversionHistorySearch {
  private Long totalPages;
  private Integer totalItems;
  private Integer currentPage;
  private List<VCurrencyConversionHistory> currencyConversionHistory;
}