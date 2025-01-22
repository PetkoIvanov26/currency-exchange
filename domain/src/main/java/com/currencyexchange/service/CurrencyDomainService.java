package com.currencyexchange.service;

import com.currencyexchange.adapter.DomainAdapter;
import com.currencyexchange.entity.Currency;
import com.currencyexchange.exception.DomainException;
import com.currencyexchange.repository.CurrencyRepository;
import com.currencyexchange.value.VCurrency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.currencyexchange.exception.errorCode.DomainErrorCode.CURRENCY_NOT_FOUND;

@Service
@Slf4j
public class CurrencyDomainService {
  private final DomainAdapter domainAdapter;
  private final CurrencyRepository currencyRepository;

  @Autowired
  public CurrencyDomainService(DomainAdapter domainAdapter, CurrencyRepository currencyRepository) {
    this.domainAdapter = domainAdapter;
    this.currencyRepository = currencyRepository;
  }

  public VCurrency findOrCreateCurrency(String currencyCode) {
    Optional<Currency> existingCurrency = currencyRepository.findByCode(currencyCode);

    if (existingCurrency.isPresent()) {
      log.info(String.format("Retrieving currency %s", currencyCode));
      return domainAdapter.fromEntityToValue(existingCurrency.get());
    }

    Currency newCurrency = Currency.builder()
      .code(currencyCode).build();

    log.info(String.format("Persisting new currency %s", currencyCode));
    return domainAdapter.fromEntityToValue(currencyRepository.save(newCurrency));
  }

  public Currency getCurrency(Long id) throws DomainException {
    return currencyRepository.findById(id).orElseThrow(
      ()-> new DomainException(CURRENCY_NOT_FOUND)
    );
  }
}
