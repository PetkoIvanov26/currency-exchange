package service;

import com.currencyexchange.adapter.GatewayAdapter;
import com.currencyexchange.dto.response.ExchangeRateResponse;
import com.currencyexchange.exception.DomainException;
import com.currencyexchange.exception.GatewayException;
import com.currencyexchange.exception.errorCode.DomainErrorCode;
import com.currencyexchange.service.ExchangeRateDomainService;
import com.currencyexchange.service.ExchangeRateService;
import com.currencyexchange.value.VExchangeRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExchangeRateServiceTest {
  @Mock
  private ExchangeRateDomainService exchangeRateDomainService;
  @Mock
  private GatewayAdapter gatewayAdapter;

  @InjectMocks
  private ExchangeRateService exchangeRateService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetExchangeRate_Success() throws GatewayException, DomainException {
    String sourceCurrency = "USD";
    String targetCurrency = "EUR";
    VExchangeRate vExchangeRate = new VExchangeRate();
    ExchangeRateResponse expectedResponse = new ExchangeRateResponse();

    when(exchangeRateDomainService.getExchangeRate(sourceCurrency, targetCurrency)).thenReturn(vExchangeRate);
    when(gatewayAdapter.fromValueToResponse(vExchangeRate)).thenReturn(expectedResponse);

    ExchangeRateResponse result = exchangeRateService.getExchangeRate(sourceCurrency, targetCurrency);

    assertNotNull(result);
    assertEquals(expectedResponse, result);
    verify(exchangeRateDomainService, times(1)).getExchangeRate(sourceCurrency, targetCurrency);
    verify(gatewayAdapter, times(1)).fromValueToResponse(vExchangeRate);
  }

  @Test
  void testGetExchangeRate_Failure_DomainException() throws DomainException {
    String sourceCurrency = "USD";
    String targetCurrency = "EUR";

    when(exchangeRateDomainService.getExchangeRate(sourceCurrency, targetCurrency))
      .thenThrow(new DomainException(DomainErrorCode.EXCHANGE_RATE_NOT_FOUND));

    GatewayException thrown = assertThrows(GatewayException.class, () -> {
      exchangeRateService.getExchangeRate(sourceCurrency, targetCurrency);
    });

    assertEquals("Exchange rate not found for %s to %s", thrown.getCause().getMessage());
    verify(exchangeRateDomainService, times(1)).getExchangeRate(sourceCurrency, targetCurrency);
    verify(gatewayAdapter, times(0)).fromValueToResponse(any(VExchangeRate.class));
  }
}
