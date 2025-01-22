package com.currencyexchange.service;

import com.currencyexchange.adapter.DomainAdapter;
import com.currencyexchange.entity.Currency;
import com.currencyexchange.exception.DomainException;
import com.currencyexchange.repository.CurrencyRepository;
import com.currencyexchange.value.VCurrency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.currencyexchange.exception.errorCode.DomainErrorCode.CURRENCY_NOT_FOUND;

@Service
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
      return domainAdapter.fromEntityToValue(existingCurrency.get());
    }

    Currency newCurrency = Currency.builder()
      .code(currencyCode).build();

    return domainAdapter.fromEntityToValue(currencyRepository.save(newCurrency));
  }

  public Currency getCurrency(Long id) throws DomainException {
    return currencyRepository.findById(id).orElseThrow(
      ()-> new DomainException(CURRENCY_NOT_FOUND)
    );
  }
}
