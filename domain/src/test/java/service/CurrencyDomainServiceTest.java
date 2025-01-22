package service;

import com.currencyexchange.adapter.DomainAdapter;
import com.currencyexchange.entity.Currency;
import com.currencyexchange.exception.DomainException;
import com.currencyexchange.repository.CurrencyRepository;
import com.currencyexchange.service.CurrencyDomainService;
import com.currencyexchange.value.VCurrency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import utils.CurrencyUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CurrencyDomainServiceTest {
  @Mock
  private DomainAdapter domainAdapter;
  @Mock
  private CurrencyRepository currencyRepository;

  @InjectMocks
  private CurrencyDomainService currencyDomainService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);}

  @Test
  void testFindOrCreateCurrency_ExistingCurrency() {
    Currency mockCurrency = CurrencyUtils.createCurrencyEntityEUR();
    VCurrency expectedVCurrency = CurrencyUtils.createCurrencyEUR();

    when(currencyRepository.findByCode("EUR")).thenReturn(Optional.of(mockCurrency));
    when(domainAdapter.fromEntityToValue(mockCurrency)).thenReturn(expectedVCurrency);

    VCurrency result = currencyDomainService.findOrCreateCurrency("EUR");

    assertNotNull(result);
    assertEquals(expectedVCurrency.getId(), result.getId());
    assertEquals(expectedVCurrency.getCode(), result.getCode());
    verify(currencyRepository, times(1)).findByCode("EUR");
    verify(domainAdapter, times(1)).fromEntityToValue(mockCurrency);
  }

  @Test
  void testFindOrCreateCurrency_NewCurrency() {
    Currency newCurrency = CurrencyUtils.createNewCurrencyEntityUSD();
    VCurrency expectedVCurrency = CurrencyUtils.createCurrencyUSD();

    when(currencyRepository.findByCode("USD")).thenReturn(Optional.empty());
    when(currencyRepository.save(any(Currency.class))).thenReturn(newCurrency);
    when(domainAdapter.fromEntityToValue(newCurrency)).thenReturn(expectedVCurrency);

    VCurrency result = currencyDomainService.findOrCreateCurrency("USD");

    assertNotNull(result);
    assertEquals(expectedVCurrency.getId(), result.getId());
    assertEquals(expectedVCurrency.getCode(), result.getCode());
    verify(currencyRepository, times(1)).findByCode("USD");
    verify(currencyRepository, times(1)).save(newCurrency);
    verify(domainAdapter, times(1)).fromEntityToValue(newCurrency);
  }

  @Test
  void testGetCurrency_ExistingCurrency() throws DomainException {
    Currency mockCurrency = CurrencyUtils.createCurrencyEntityEUR();

    when(currencyRepository.findById(1L)).thenReturn(Optional.of(mockCurrency));

    Currency result = currencyDomainService.getCurrency(1L);

    assertNotNull(result);
    assertEquals(mockCurrency.getId(), result.getId());
    assertEquals(mockCurrency.getCode(), result.getCode());
    verify(currencyRepository, times(1)).findById(1L);
  }

  @Test
  void testGetCurrency_CurrencyNotFound() {
    when(currencyRepository.findById(1L)).thenReturn(Optional.empty());

    DomainException thrown = assertThrows(DomainException.class, () -> {
      currencyDomainService.getCurrency(1L);
    });

    assertEquals("Currency not found", thrown.getMessage());
    verify(currencyRepository, times(1)).findById(1L);
  }

}
