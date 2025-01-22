package com.currencyexchange.adapter;

import com.currencyexchange.dto.request.CurrencyConversionHistorySearchRequest;
import com.currencyexchange.dto.response.CurrencyConversionHistoryResponse;
import com.currencyexchange.dto.response.CurrencyConversionHistorySearch;
import com.currencyexchange.dto.response.CurrencyResponse;
import com.currencyexchange.dto.response.ExchangeRateResponse;
import com.currencyexchange.value.VCurrency;
import com.currencyexchange.value.VCurrencyConversionHistory;
import com.currencyexchange.value.VCurrencyConversionHistorySearch;
import com.currencyexchange.value.VExchangeRate;
import com.currencyexchange.value.request.VCurrencyConversionHistorySearchRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GatewayAdapter {

  ExchangeRateResponse fromValueToResponse(VExchangeRate exchangeRate);
  CurrencyResponse fromValueToResponse(VCurrency vCurrency);
  CurrencyConversionHistoryResponse fromValueToResponse(
    VCurrencyConversionHistory convertedCurrency);

  VCurrencyConversionHistorySearchRequest fromRequestToValue(CurrencyConversionHistorySearchRequest searchRequest);

  CurrencyConversionHistorySearch fromValueToResponse(VCurrencyConversionHistorySearch searchConversionHistory);
}
