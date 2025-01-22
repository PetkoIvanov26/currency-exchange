package service;

import com.currencyexchange.adapter.GatewayAdapter;
import com.currencyexchange.dto.request.CurrencyConversionHistorySearchRequest;
import com.currencyexchange.dto.request.CurrencyConversionRequest;
import com.currencyexchange.dto.response.CurrencyConversionHistoryResponse;
import com.currencyexchange.dto.response.CurrencyConversionHistorySearch;
import com.currencyexchange.exception.DomainException;
import com.currencyexchange.exception.GatewayException;
import com.currencyexchange.exception.errorCode.DomainErrorCode;
import com.currencyexchange.service.CurrencyConversionDomainService;
import com.currencyexchange.service.CurrencyConversionService;
import com.currencyexchange.service.ExchangeRateDomainService;
import com.currencyexchange.value.VCurrency;
import com.currencyexchange.value.VCurrencyConversionHistory;
import com.currencyexchange.value.VCurrencyConversionHistorySearch;
import com.currencyexchange.value.VExchangeRate;
import com.currencyexchange.value.request.VCurrencyConversionHistoryRequest;
import com.currencyexchange.value.request.VCurrencyConversionHistorySearchRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CurrencyConversionServiceTest {
  @Mock
  private GatewayAdapter adapter;
  @Mock
  private ExchangeRateDomainService exchangeRateDomainService;
  @Mock
  private CurrencyConversionDomainService currencyConversionDomainService;

  @InjectMocks
  private CurrencyConversionService currencyConversionService;

  private CurrencyConversionRequest currencyConversionRequest;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Prepare request for currency conversion
    currencyConversionRequest = new CurrencyConversionRequest();
    currencyConversionRequest.setSourceCurrencyCode("USD");
    currencyConversionRequest.setTargetCurrencyCode("EUR");
    currencyConversionRequest.setAmount(new BigDecimal("100"));
  }

  @Test
  void testCurrencyConversion_Success() throws GatewayException, DomainException {
    // Prepare mock data
    VExchangeRate exchangeRate = new VExchangeRate();
    exchangeRate.setExchangeRate(new BigDecimal("0.85"));
    exchangeRate.setSourceCurrency(new VCurrency());
    exchangeRate.setTargetCurrency(new VCurrency());

    VCurrencyConversionHistoryRequest vCurrencyConversionHistoryRequest = new VCurrencyConversionHistoryRequest();
    vCurrencyConversionHistoryRequest.setAmount(currencyConversionRequest.getAmount());
    vCurrencyConversionHistoryRequest.setConvertedAmount(currencyConversionRequest.getAmount().multiply(exchangeRate.getExchangeRate()));

    VCurrencyConversionHistory convertedCurrency = new VCurrencyConversionHistory();

    when(exchangeRateDomainService.getExchangeRate("USD", "EUR")).thenReturn(exchangeRate);
    when(currencyConversionDomainService.createCurrencyConversionHistory(vCurrencyConversionHistoryRequest)).thenReturn(convertedCurrency);
    when(adapter.fromValueToResponse(convertedCurrency)).thenReturn(new CurrencyConversionHistoryResponse());

    CurrencyConversionHistoryResponse result = currencyConversionService.currencyConversion(currencyConversionRequest);

    assertNotNull(result);
    verify(exchangeRateDomainService, times(1)).getExchangeRate("USD", "EUR");
    verify(currencyConversionDomainService, times(1)).createCurrencyConversionHistory(vCurrencyConversionHistoryRequest);
    verify(adapter, times(1)).fromValueToResponse(convertedCurrency);
  }

  @Test
  void testCurrencyConversion_ExchangeRateNotFound() throws DomainException {
    when(exchangeRateDomainService.getExchangeRate("USD", "EUR")).thenThrow(new DomainException(DomainErrorCode.EXCHANGE_RATE_NOT_FOUND));

    GatewayException thrown = assertThrows(GatewayException.class, () -> {
      currencyConversionService.currencyConversion(currencyConversionRequest);
    });

    assertEquals("Exchange rate not found for %s to %s", thrown.getCause().getMessage());
    verify(exchangeRateDomainService, times(1)).getExchangeRate("USD", "EUR");
  }

  @Test
  void testCurrencyConversion_CurrencyConversionHistoryNotFound() throws DomainException {
    VExchangeRate exchangeRate = new VExchangeRate();
    exchangeRate.setExchangeRate(new BigDecimal("0.85"));
    exchangeRate.setSourceCurrency(new VCurrency());
    exchangeRate.setTargetCurrency(new VCurrency());

    VCurrencyConversionHistoryRequest vCurrencyConversionHistoryRequest = new VCurrencyConversionHistoryRequest();
    vCurrencyConversionHistoryRequest.setAmount(currencyConversionRequest.getAmount());
    vCurrencyConversionHistoryRequest.setConvertedAmount(currencyConversionRequest.getAmount().multiply(exchangeRate.getExchangeRate()));

    when(exchangeRateDomainService.getExchangeRate("USD", "EUR")).thenReturn(exchangeRate);
    when(currencyConversionDomainService.createCurrencyConversionHistory(vCurrencyConversionHistoryRequest)).thenThrow(new DomainException(
      DomainErrorCode.CURRENCY_NOT_FOUND));

    GatewayException thrown = assertThrows(GatewayException.class, () -> {
      currencyConversionService.currencyConversion(currencyConversionRequest);
    });

    assertEquals("Currency not found", thrown.getCause().getMessage());
    verify(exchangeRateDomainService, times(1)).getExchangeRate("USD", "EUR");
    verify(currencyConversionDomainService, times(1)).createCurrencyConversionHistory(vCurrencyConversionHistoryRequest);
  }

  @Test
  void testSearchConversionHistory() {
    CurrencyConversionHistorySearchRequest searchRequest = new CurrencyConversionHistorySearchRequest();
    VCurrencyConversionHistorySearchRequest vSearchRequest = new VCurrencyConversionHistorySearchRequest();

    VCurrencyConversionHistorySearch mockSearchResponse = new VCurrencyConversionHistorySearch();

    when(adapter.fromRequestToValue(searchRequest)).thenReturn(vSearchRequest);
    when(currencyConversionDomainService.searchConversionHistory(vSearchRequest)).thenReturn(mockSearchResponse);
    when(adapter.fromValueToResponse(mockSearchResponse)).thenReturn(new CurrencyConversionHistorySearch());

    CurrencyConversionHistorySearch result = currencyConversionService.searchConversionHistory(searchRequest);

    assertNotNull(result);
    verify(adapter, times(1)).fromRequestToValue(searchRequest);
    verify(currencyConversionDomainService, times(1)).searchConversionHistory(vSearchRequest);
    verify(adapter, times(1)).fromValueToResponse(mockSearchResponse);
  }
}
