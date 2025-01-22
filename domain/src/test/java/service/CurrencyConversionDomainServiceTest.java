package service;
import com.currencyexchange.adapter.DomainAdapter;
import com.currencyexchange.entity.Currency;
import com.currencyexchange.entity.CurrencyConversionHistory;
import com.currencyexchange.exception.DomainException;
import com.currencyexchange.exception.errorCode.DomainErrorCode;
import com.currencyexchange.repository.ConversionHistoryRepository;
import com.currencyexchange.service.CurrencyConversionDomainService;
import com.currencyexchange.service.CurrencyDomainService;
import com.currencyexchange.value.VCurrencyConversionHistory;
import com.currencyexchange.value.VCurrencyConversionHistorySearch;
import com.currencyexchange.value.request.VCurrencyConversionHistoryRequest;
import com.currencyexchange.value.request.VCurrencyConversionHistorySearchRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import utils.CurrencyUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CurrencyConversionDomainServiceTest {
  @Mock
  private DomainAdapter domainAdapter;
  @Mock
  private ConversionHistoryRepository conversionHistoryRepository;
  @Mock
  private CurrencyDomainService currencyDomainService;
  @InjectMocks
  private CurrencyConversionDomainService currencyConversionDomainService;

  private Currency currencyFrom;
  private Currency currencyTo;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    currencyFrom = CurrencyUtils.createCurrencyEntityUSD();
    currencyTo = CurrencyUtils.createCurrencyEntityEUR();
  }

  @Test
  void testCreateCurrencyConversionHistory_SUCCESS() throws DomainException {
    VCurrencyConversionHistoryRequest request = new VCurrencyConversionHistoryRequest();
    request.setSourceCurrencyId(currencyFrom.getId());
    request.setTargetCurrencyId(currencyTo.getId());
    request.setAmount(new BigDecimal("100"));
    request.setConvertedAmount(new BigDecimal("84"));

    when(currencyDomainService.getCurrency(currencyFrom.getId())).thenReturn(currencyFrom);
    when(currencyDomainService.getCurrency(currencyTo.getId())).thenReturn(currencyTo);
    when(conversionHistoryRepository.save(any(CurrencyConversionHistory.class)))
      .thenReturn(new CurrencyConversionHistory());
    when(domainAdapter.fromEntityToValue(any(CurrencyConversionHistory.class)))
      .thenReturn(new VCurrencyConversionHistory());

    VCurrencyConversionHistory result = currencyConversionDomainService.createCurrencyConversionHistory(request);

    assertNotNull(result);
    verify(conversionHistoryRepository, times(1)).save(any(CurrencyConversionHistory.class));
    verify(domainAdapter, times(1)).fromEntityToValue(any(CurrencyConversionHistory.class));
  }

  @Test
  void testCreateCurrencyConversionHistory_ThrowsDomainException() throws DomainException {
    VCurrencyConversionHistoryRequest request = new VCurrencyConversionHistoryRequest();
    request.setSourceCurrencyId(currencyFrom.getId());
    request.setTargetCurrencyId(currencyTo.getId());
    request.setAmount(new BigDecimal("100"));
    request.setConvertedAmount(new BigDecimal("84"));

    when(currencyDomainService.getCurrency(currencyFrom.getId()))
      .thenThrow(new DomainException(DomainErrorCode.CURRENCY_NOT_FOUND));

    DomainException thrown = assertThrows(DomainException.class, () ->
      currencyConversionDomainService.createCurrencyConversionHistory(request)
    );

    assertEquals("Currency not found", thrown.getMessage());
  }

  @Test
  void testSearchConversionHistory_EmptyResults() throws DomainException {

    VCurrencyConversionHistorySearchRequest request = new VCurrencyConversionHistorySearchRequest();
    request.setSourceCurrencyCode("USD");
    request.setTargetCurrencyCode("EUR");
    request.setPage(12);
    request.setRecords(10);
;

    when(currencyDomainService.getCurrency(currencyFrom.getId())).thenReturn(currencyFrom);
    when(currencyDomainService.getCurrency(currencyTo.getId())).thenReturn(currencyTo);

    CurrencyConversionHistory savedEntity = new CurrencyConversionHistory();
    savedEntity.setTransactionId("mock-transaction-id");
    savedEntity.setSourceCurrency(currencyFrom);
    savedEntity.setTargetCurrency(currencyTo);
    savedEntity.setAmount(new BigDecimal("100"));
    savedEntity.setConvertedAmount(new BigDecimal("84"));
    savedEntity.setTimestamp(LocalDateTime.now());

    when(conversionHistoryRepository.findAll(any(Specification.class), any(Pageable.class)))
      .thenReturn(Page.empty());

    when(domainAdapter.fromEntityToValue(anyLong(), anyInt(), anyInt(), anyList()))
      .thenReturn(new VCurrencyConversionHistorySearch(
        1L,
        1,
        12,
        List.of()
      ));

    VCurrencyConversionHistorySearch result = currencyConversionDomainService.searchConversionHistory(request);

    assertNotNull(result);
    assertEquals(1, result.getTotalItems());
    assertEquals(1, result.getTotalPages());
    assertEquals(12, result.getCurrentPage());
  }
}
