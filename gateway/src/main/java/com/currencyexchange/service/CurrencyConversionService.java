package com.currencyexchange.service;

import com.currencyexchange.adapter.GatewayAdapter;
import com.currencyexchange.dto.request.CurrencyConversionHistorySearchRequest;
import com.currencyexchange.dto.request.CurrencyConversionRequest;
import com.currencyexchange.dto.response.CurrencyConversionHistoryResponse;
import com.currencyexchange.dto.response.CurrencyConversionHistorySearch;
import com.currencyexchange.exception.DomainException;
import com.currencyexchange.exception.GatewayException;
import com.currencyexchange.value.VCurrencyConversionHistory;
import com.currencyexchange.value.VExchangeRate;
import com.currencyexchange.value.request.VCurrencyConversionHistoryRequest;
import com.currencyexchange.value.request.VCurrencyConversionHistorySearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.currencyexchange.exception.errorCode.GatewayErrorCode.CURRENCY_NOT_FOUND;
import static com.currencyexchange.exception.errorCode.GatewayErrorCode.EXCHANGE_RATE_NOT_FOUND;

@Service
public class CurrencyConversionService {
  private final GatewayAdapter adapter;
  private final ExchangeRateDomainService exchangeRateDomainService;
  private final CurrencyConversionDomainService currencyConversionDomainService;

  @Autowired
  public CurrencyConversionService(
    GatewayAdapter adapter, ExchangeRateDomainService exchangeRateDomainService,
    CurrencyConversionDomainService currencyConversionDomainService) {
    this.adapter = adapter;
    this.exchangeRateDomainService = exchangeRateDomainService;
    this.currencyConversionDomainService = currencyConversionDomainService;
  }

  public CurrencyConversionHistoryResponse currencyConversion(CurrencyConversionRequest request) throws
    GatewayException {

    VExchangeRate exchangeRate = null;
    try {
      exchangeRate = exchangeRateDomainService.getExchangeRate(request.getSourceCurrencyCode().toUpperCase()
        , request.getTargetCurrencyCode().toUpperCase());
    } catch (DomainException e) {
      throw new GatewayException(EXCHANGE_RATE_NOT_FOUND,e);
    }

    BigDecimal finalAmount = request.getAmount().multiply(exchangeRate.getExchangeRate());

    VCurrencyConversionHistoryRequest vCurrencyConversionHistoryRequest = new VCurrencyConversionHistoryRequest();
    vCurrencyConversionHistoryRequest.setSourceCurrencyId(exchangeRate.getSourceCurrency().getId());
    vCurrencyConversionHistoryRequest.setTargetCurrencyId(exchangeRate.getTargetCurrency().getId());
    vCurrencyConversionHistoryRequest.setAmount(request.getAmount());
    vCurrencyConversionHistoryRequest.setConvertedAmount(finalAmount);

    VCurrencyConversionHistory
      convertedCurrency = null;
    try {
      convertedCurrency =
        currencyConversionDomainService.createCurrencyConversionHistory(vCurrencyConversionHistoryRequest);
    } catch (DomainException e) {
      throw new GatewayException(CURRENCY_NOT_FOUND,e);
    }

    return adapter.fromValueToResponse(convertedCurrency);
  }

  public CurrencyConversionHistorySearch searchConversionHistory(CurrencyConversionHistorySearchRequest searchRequest) {
    VCurrencyConversionHistorySearchRequest vSearchRequest = adapter.fromRequestToValue(searchRequest);
    return adapter.fromValueToResponse(currencyConversionDomainService.searchConversionHistory(vSearchRequest));
  }
}
