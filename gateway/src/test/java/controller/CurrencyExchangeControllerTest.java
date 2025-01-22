package controller;

import com.currencyexchange.adapter.GatewayAdapter;
import com.currencyexchange.controller.CurrencyExchangeController;
import com.currencyexchange.dto.request.CurrencyConversionHistorySearchRequest;
import com.currencyexchange.dto.request.CurrencyConversionRequest;
import com.currencyexchange.dto.response.CurrencyConversionHistoryResponse;
import com.currencyexchange.dto.response.CurrencyConversionHistorySearch;
import com.currencyexchange.dto.response.ExchangeRateResponse;
import com.currencyexchange.exception.DomainException;
import com.currencyexchange.exception.GatewayException;
import com.currencyexchange.exception.errorCode.GatewayErrorCode;
import com.currencyexchange.service.CurrencyConversionService;
import com.currencyexchange.service.ExchangeRateDomainService;
import com.currencyexchange.service.ExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CurrencyExchangeControllerTest {
  @Mock
  private CurrencyConversionService currencyConversionService;
  @Mock
  private ExchangeRateService exchangeRateService;

  @InjectMocks
  private CurrencyExchangeController currencyExchangeController;

  private CurrencyConversionRequest conversionRequest;
  private CurrencyConversionHistorySearchRequest searchRequest;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    conversionRequest = new CurrencyConversionRequest();
    conversionRequest.setSourceCurrencyCode("USD");
    conversionRequest.setTargetCurrencyCode("EUR");
    conversionRequest.setAmount(new BigDecimal("100"));

    searchRequest = new CurrencyConversionHistorySearchRequest();
    searchRequest.setSourceCurrencyCode("USD");
    searchRequest.setTargetCurrencyCode("EUR");
    searchRequest.setPage(1);
    searchRequest.setRecords(10);
  }

  @Test
  void testGetExchangeRate_Success() throws GatewayException {
    ExchangeRateResponse expectedResponse = new ExchangeRateResponse();
    expectedResponse.setExchangeRate(new BigDecimal("0.85"));

    when(exchangeRateService.getExchangeRate("USD", "EUR")).thenReturn(expectedResponse);

    ResponseEntity<ExchangeRateResponse> result = currencyExchangeController.getExchangeRate("USD", "EUR");

    assertNotNull(result);
    assertEquals(200, result.getStatusCodeValue());
    assertEquals(expectedResponse, result.getBody());
    verify(exchangeRateService, times(1)).getExchangeRate("USD", "EUR");
  }

  @Test
  void testGetExchangeRate_Failure() throws GatewayException {
    when(exchangeRateService.getExchangeRate("USD", "EUR")).thenThrow(new GatewayException(GatewayErrorCode.EXCHANGE_RATE_NOT_FOUND));

    GatewayException thrown = assertThrows(GatewayException.class, () -> {
      currencyExchangeController.getExchangeRate("USD", "EUR");
    });

    assertEquals("Exchange rate not found", thrown.getMessage());
    verify(exchangeRateService, times(1)).getExchangeRate("USD", "EUR");
  }

  @Test
  void testConvertCurrency_Success() throws GatewayException {
    CurrencyConversionHistoryResponse expectedResponse = new CurrencyConversionHistoryResponse();
    expectedResponse.setTransactionId("txn-12345");
    expectedResponse.setConvertedAmount(new BigDecimal("85"));

    when(currencyConversionService.currencyConversion(conversionRequest)).thenReturn(expectedResponse);

    ResponseEntity<CurrencyConversionHistoryResponse> result = currencyExchangeController.convertCurrency(conversionRequest);

    assertNotNull(result);
    assertEquals(200, result.getStatusCodeValue());
    assertEquals(expectedResponse, result.getBody());
    verify(currencyConversionService, times(1)).currencyConversion(conversionRequest);
  }

  @Test
  void testConvertCurrency_Failure() throws GatewayException {
    when(currencyConversionService.currencyConversion(conversionRequest)).thenThrow(new GatewayException(GatewayErrorCode.CURRENCY_NOT_FOUND));

    GatewayException thrown = assertThrows(GatewayException.class, () -> {
      currencyExchangeController.convertCurrency(conversionRequest);
    });

    assertEquals("Currency not found", thrown.getMessage());
    verify(currencyConversionService, times(1)).currencyConversion(conversionRequest);
  }

  @Test
  void testGetConversionHistory_Success() {
    CurrencyConversionHistorySearch expectedResponse = new CurrencyConversionHistorySearch();
    expectedResponse.setCurrentPage(1);
    expectedResponse.setTotalPages(1L);
    expectedResponse.setTotalItems(1);

    when(currencyConversionService.searchConversionHistory(searchRequest)).thenReturn(expectedResponse);

    ResponseEntity<CurrencyConversionHistorySearch> result = currencyExchangeController.getConversionHistory(
      "USD", "EUR", null, null, null, 1, 10);

    assertNotNull(result);
    assertEquals(200, result.getStatusCodeValue());
    assertEquals(expectedResponse, result.getBody());
    verify(currencyConversionService, times(1)).searchConversionHistory(searchRequest);
  }

  @Test
  void testGetConversionHistory_Failure() {
    when(currencyConversionService.searchConversionHistory(searchRequest)).thenThrow(new RuntimeException("Failed to fetch conversion history"));

    RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
      currencyExchangeController.getConversionHistory(
        "USD", "EUR", null, null, null, 1, 10);
    });

    assertEquals("Failed to fetch conversion history", thrown.getMessage());
    verify(currencyConversionService, times(1)).searchConversionHistory(searchRequest);
  }
}
