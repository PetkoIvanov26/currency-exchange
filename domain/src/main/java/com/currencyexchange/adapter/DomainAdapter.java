package com.currencyexchange.adapter;

import com.currencyexchange.entity.Currency;
import com.currencyexchange.entity.CurrencyConversionHistory;
import com.currencyexchange.entity.ExchangeRate;
import com.currencyexchange.value.VCurrency;
import com.currencyexchange.value.VCurrencyConversionHistory;
import com.currencyexchange.value.VCurrencyConversionHistorySearch;
import com.currencyexchange.value.VExchangeRate;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DomainAdapter {
  VExchangeRate fromEntityToValue(ExchangeRate exchangeRate);

  VCurrency fromEntityToValue(Currency currency);

  VCurrencyConversionHistory fromEntityToValue(CurrencyConversionHistory currencyConversionHistory);

  VCurrencyConversionHistorySearch fromEntityToValue(Long totalItems,Integer totalPages,Integer currentPage, List<VCurrencyConversionHistory> currencyConversionHistory);
}
