package com.currencyexchange.service;

import com.currencyexchange.adapter.GatewayAdapter;
import com.currencyexchange.dto.response.ExchangeRateResponse;
import com.currencyexchange.exception.DomainException;
import com.currencyexchange.exception.GatewayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.currencyexchange.exception.errorCode.GatewayErrorCode.EXCHANGE_RATE_NOT_FOUND;

@Service
public class ExchangeRateService {
  private final ExchangeRateDomainService exchangeRateDomainService;
  private final GatewayAdapter gatewayAdapter;

  @Autowired
  public ExchangeRateService(ExchangeRateDomainService exchangeRateDomainService, GatewayAdapter gatewayAdapter) {
    this.exchangeRateDomainService = exchangeRateDomainService;
    this.gatewayAdapter = gatewayAdapter;
  }

  public ExchangeRateResponse getExchangeRate(String sourceCurrency, String targetCurrency) throws GatewayException {
    try {
      return gatewayAdapter.fromValueToResponse(exchangeRateDomainService.getExchangeRate(sourceCurrency, targetCurrency));
    } catch (DomainException e) {
      throw new GatewayException(EXCHANGE_RATE_NOT_FOUND,e);
    }
  }
}
