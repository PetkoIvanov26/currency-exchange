package service;

import com.currencyexchange.adapter.DomainAdapter;
import com.currencyexchange.entity.Currency;
import com.currencyexchange.entity.ExchangeRate;
import com.currencyexchange.exception.DomainException;
import com.currencyexchange.repository.ExchangeRateRepository;
import com.currencyexchange.service.CurrencyDomainService;
import com.currencyexchange.service.ExchangeRateDomainService;
import com.currencyexchange.value.VCurrency;
import com.currencyexchange.value.VExchangeRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import utils.CurrencyUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExchangeRateDomainServiceTest {

  @Mock
  private DomainAdapter domainAdapter;
  @Mock
  private ExchangeRateRepository exchangeRateRepository;
  @Mock
  private CurrencyDomainService currencyDomainService;

  @InjectMocks
  private ExchangeRateDomainService exchangeRateDomainService;

  private Currency currencyFrom;
  private Currency currencyTo;
  private ExchangeRate exchangeRate;
  private ExchangeRate exchangeRateFromToEur;
  private ExchangeRate exchangeRateEurToTarget;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    currencyFrom = new Currency(1L, "USD");
    currencyTo = new Currency(2L, "EUR");

    exchangeRate = new ExchangeRate();
    exchangeRate.setSourceCurrency(currencyFrom);
    exchangeRate.setTargetCurrency(currencyTo);
    exchangeRate.setExchangeRate(new BigDecimal("0.84"));
    exchangeRate.setTimestamp(LocalDateTime.now());

    exchangeRateFromToEur = new ExchangeRate();
    exchangeRateFromToEur.setSourceCurrency(currencyFrom);
    exchangeRateFromToEur.setTargetCurrency(new Currency(3L, "EUR"));  // EUR
    exchangeRateFromToEur.setExchangeRate(new BigDecimal("1.2"));
    exchangeRateFromToEur.setTimestamp(LocalDateTime.now());

    exchangeRateEurToTarget = new ExchangeRate();
    exchangeRateEurToTarget.setSourceCurrency(new Currency(3L, "EUR")); // EUR
    exchangeRateEurToTarget.setTargetCurrency(currencyTo);
    exchangeRateEurToTarget.setExchangeRate(new BigDecimal("0.9"));
    exchangeRateEurToTarget.setTimestamp(LocalDateTime.now());
  }

  @Test
  void testSaveOrUpdateExchangeRate_Update_SUCCESS() throws DomainException {
    when(exchangeRateRepository.findBySourceCurrencyIdAndTargetCurrencyId(anyLong(), anyLong()))
      .thenReturn(Optional.of(exchangeRate));

    BigDecimal newRate = new BigDecimal("0.85");

    exchangeRateDomainService.saveOrUpdateExchangeRate(1L, 2L, newRate);

    verify(exchangeRateRepository, times(1)).save(exchangeRate);
    assertEquals(newRate, exchangeRate.getExchangeRate());
  }

  @Test
  void testSaveOrUpdateExchangeRate_Create_SUCCESS() throws DomainException {
    when(exchangeRateRepository.findBySourceCurrencyIdAndTargetCurrencyId(anyLong(), anyLong()))
      .thenReturn(Optional.empty());
    when(currencyDomainService.getCurrency(anyLong())).thenReturn(currencyFrom);
    when(currencyDomainService.getCurrency(anyLong())).thenReturn(currencyTo);
    when(domainAdapter.fromEntityToValue(any(ExchangeRate.class))).thenReturn(new VExchangeRate());

    exchangeRateDomainService.saveOrUpdateExchangeRate(1L, 2L, new BigDecimal("0.85"));

    verify(exchangeRateRepository, times(1)).save(any(ExchangeRate.class));
  }

  @Test
  void testGetExchangeRate_EUR_TO_OTHER_CURRENCY_SUCCESS() throws DomainException {
    VCurrency vCurrencyFrom = CurrencyUtils.createCurrencyUSD();
    VCurrency vCurrencyTo = CurrencyUtils.createCurrencyEUR();
    VExchangeRate vExchangeRate = new VExchangeRate();
    vExchangeRate.setSourceCurrency(vCurrencyFrom);
    vExchangeRate.setTargetCurrency(vCurrencyTo);
    vExchangeRate.setExchangeRate(new BigDecimal("0.84"));

    when(currencyDomainService.findOrCreateCurrency("EUR")).thenReturn(vCurrencyFrom);
    when(currencyDomainService.findOrCreateCurrency("USD")).thenReturn(vCurrencyTo);
    when(exchangeRateRepository.findBySourceCurrencyIdAndTargetCurrencyId(anyLong(), anyLong()))
      .thenReturn(Optional.of(exchangeRate));

    VExchangeRate result = exchangeRateDomainService.getExchangeRate("EUR","USD");

    assertNotNull(result);
    assertEquals(vExchangeRate.getSourceCurrency().getCode(), result.getSourceCurrency().getCode());
    assertEquals(vExchangeRate.getTargetCurrency().getCode(), result.getTargetCurrency().getCode());
    assertEquals(vExchangeRate.getExchangeRate(), result.getExchangeRate());
  }

  @Test
  void testGetExchangeRate_SAME_CURRENCY_SUCCESS() throws DomainException {
    VCurrency vCurrencyFrom = CurrencyUtils.createCurrencyUSD();
    VExchangeRate vExchangeRate = new VExchangeRate();
    vExchangeRate.setSourceCurrency(vCurrencyFrom);
    vExchangeRate.setTargetCurrency(vCurrencyFrom);
    vExchangeRate.setExchangeRate(new BigDecimal("1"));

    when(currencyDomainService.findOrCreateCurrency("USD")).thenReturn(vCurrencyFrom);

    VExchangeRate result = exchangeRateDomainService.getExchangeRate("USD","USD");

    assertNotNull(result);
    assertEquals(vExchangeRate.getSourceCurrency().getCode(), result.getSourceCurrency().getCode());
    assertEquals(vExchangeRate.getTargetCurrency().getCode(), result.getTargetCurrency().getCode());
    assertEquals(vExchangeRate.getExchangeRate(), result.getExchangeRate());
  }

  @Test
  void testGetExchangeRate_BETWEEEN_TWO_DIFFERENT_CURRENCIES_THAN_EUR_SUCCESS() throws DomainException {
    VCurrency vCurrencyFrom = CurrencyUtils.createCurrencyBGR();
    VCurrency vCurrencyTo = CurrencyUtils.createCurrencyUSD();
    VExchangeRate vExchangeRate = new VExchangeRate();
    vExchangeRate.setSourceCurrency(vCurrencyFrom);
    vExchangeRate.setTargetCurrency(vCurrencyTo);
    vExchangeRate.setExchangeRate(new BigDecimal("0.7499997"));

    when(currencyDomainService.findOrCreateCurrency("BGN")).thenReturn(vCurrencyFrom);
    when(currencyDomainService.findOrCreateCurrency("USD")).thenReturn(vCurrencyTo);
    when(currencyDomainService.findOrCreateCurrency("EUR")).thenReturn(CurrencyUtils.createCurrencyEUR());


    when(exchangeRateRepository.findBySourceCurrencyIdAndTargetCurrencyId(anyLong(), eq(3L)))
      .thenReturn(Optional.of(exchangeRateFromToEur)); // USD -> EUR

    when(exchangeRateRepository.findBySourceCurrencyIdAndTargetCurrencyId(anyLong(), eq(2L)))
      .thenReturn(Optional.of(exchangeRateEurToTarget));

    VExchangeRate result = exchangeRateDomainService.getExchangeRate("BGN","USD");

    assertNotNull(result);
    assertEquals(vExchangeRate.getSourceCurrency().getCode(), result.getSourceCurrency().getCode());
    assertEquals(vExchangeRate.getTargetCurrency().getCode(), result.getTargetCurrency().getCode());
    assertEquals(vExchangeRate.getExchangeRate(), result.getExchangeRate());
  }

  @Test
  void testGetExchangeRate_SUCCESS() throws DomainException {
    VCurrency vCurrencyFrom = CurrencyUtils.createCurrencyUSD();
    VCurrency vCurrencyTo = CurrencyUtils.createCurrencyEUR();
    VExchangeRate vExchangeRate = new VExchangeRate();
    vExchangeRate.setSourceCurrency(vCurrencyFrom);
    vExchangeRate.setTargetCurrency(vCurrencyTo);
    vExchangeRate.setExchangeRate(new BigDecimal("1.190476"));

    when(currencyDomainService.findOrCreateCurrency("USD")).thenReturn(vCurrencyFrom);
    when(currencyDomainService.findOrCreateCurrency("EUR")).thenReturn(vCurrencyTo);
    when(exchangeRateRepository.findBySourceCurrencyIdAndTargetCurrencyId(anyLong(), anyLong()))
      .thenReturn(Optional.of(exchangeRate));

    VExchangeRate result = exchangeRateDomainService.getExchangeRate("USD", "EUR");

    assertNotNull(result);
    assertEquals(vExchangeRate.getSourceCurrency().getCode(), result.getSourceCurrency().getCode());
    assertEquals(vExchangeRate.getTargetCurrency().getCode(), result.getTargetCurrency().getCode());
    assertEquals(vExchangeRate.getExchangeRate(), result.getExchangeRate());
  }

  @Test
  void testGetExchangeRate_ExchangeRateNotFoundError() {
    when(currencyDomainService.findOrCreateCurrency("USD")).thenReturn(new VCurrency());
    when(currencyDomainService.findOrCreateCurrency("EUR")).thenReturn(new VCurrency());
    when(exchangeRateRepository.findBySourceCurrencyIdAndTargetCurrencyId(anyLong(), anyLong()))
      .thenReturn(Optional.empty());

    DomainException thrown = assertThrows(DomainException.class, () ->
      exchangeRateDomainService.getExchangeRate("USD", "EUR")
    );

    assertEquals(1002, thrown.getCode());
  }
  @Test
  void testGetExchangeRate_ExchangeRateNotFoundError_EUR_TO_OTHER_CURRENCY() {
    when(currencyDomainService.findOrCreateCurrency("EUR")).thenReturn(new VCurrency());
    when(currencyDomainService.findOrCreateCurrency("USD")).thenReturn(new VCurrency());
    when(exchangeRateRepository.findBySourceCurrencyIdAndTargetCurrencyId(anyLong(), anyLong()))
      .thenReturn(Optional.empty());

    DomainException thrown = assertThrows(DomainException.class, () ->
      exchangeRateDomainService.getExchangeRate("EUR", "USD")
    );

    assertEquals(1002, thrown.getCode());
  }
  @Test
  void testGetExchangeRate_BETWEEEN_TWO_DIFFERENT_CURRENCIES_THAN_EUR_ERROR() throws DomainException {
    VCurrency vCurrencyFrom = CurrencyUtils.createCurrencyBGR();
    VCurrency vCurrencyTo = CurrencyUtils.createCurrencyUSD();
    VExchangeRate vExchangeRate = new VExchangeRate();
    vExchangeRate.setSourceCurrency(vCurrencyFrom);
    vExchangeRate.setTargetCurrency(vCurrencyTo);
    vExchangeRate.setExchangeRate(new BigDecimal("0.7499997"));

    when(currencyDomainService.findOrCreateCurrency("BGN")).thenReturn(vCurrencyFrom);
    when(currencyDomainService.findOrCreateCurrency("USD")).thenReturn(vCurrencyTo);
    when(currencyDomainService.findOrCreateCurrency("EUR")).thenReturn(CurrencyUtils.createCurrencyEUR());


    when(exchangeRateRepository.findBySourceCurrencyIdAndTargetCurrencyId(anyLong(), eq(3L)))
      .thenReturn(Optional.empty()); // USD -> EUR

    when(exchangeRateRepository.findBySourceCurrencyIdAndTargetCurrencyId(anyLong(), eq(2L)))
      .thenReturn(Optional.empty());

    DomainException thrown = assertThrows(DomainException.class, () ->
      exchangeRateDomainService.getExchangeRate("USD", "BGN")
    );

    assertEquals(1002, thrown.getCode());
  }
}
