package com.currencyexchange.service;

import com.currencyexchange.adapter.DomainAdapter;
import com.currencyexchange.entity.Currency;
import com.currencyexchange.entity.ExchangeRate;
import com.currencyexchange.exception.DomainException;
import com.currencyexchange.repository.ExchangeRateRepository;
import com.currencyexchange.value.VCurrency;
import com.currencyexchange.value.VExchangeRate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.currencyexchange.exception.errorCode.DomainErrorCode.EXCHANGE_RATE_NOT_FOUND;

@Service
@Slf4j
public class ExchangeRateDomainService {
  private final DomainAdapter domainAdapter;
  private final ExchangeRateRepository exchangeRateRepository;
  private final CurrencyDomainService currencyDomainService;

  @Autowired
  public ExchangeRateDomainService(
    DomainAdapter domainAdapter, ExchangeRateRepository exchangeRateRepository,
    CurrencyDomainService currencyDomainService) {
    this.domainAdapter = domainAdapter;
    this.exchangeRateRepository = exchangeRateRepository;
    this.currencyDomainService = currencyDomainService;
  }

  public void saveOrUpdateExchangeRate(Long fromCurrency, Long toCurrency, BigDecimal exchangeRate) throws
    DomainException {
    Optional<ExchangeRate> existingRate = exchangeRateRepository.findBySourceCurrencyIdAndTargetCurrencyId(fromCurrency, toCurrency);

    if (existingRate.isPresent()) {
      log.info(String.format("Updating exchange rate by id %d",existingRate.get().getId()));

      ExchangeRate rate = existingRate.get();
      rate.setExchangeRate(exchangeRate);
      rate.setTimestamp(LocalDateTime.now());
      exchangeRateRepository.save(rate);
    } else {

      createExchangeRate(fromCurrency, toCurrency, exchangeRate);
    }
  }

  public VExchangeRate createExchangeRate(Long fromCurrency, Long toCurrency, BigDecimal exchangeRate) throws
    DomainException {
    Currency currencyFrom = currencyDomainService.getCurrency(fromCurrency);
    Currency currencyTo = currencyDomainService.getCurrency(toCurrency);

    ExchangeRate newRate = ExchangeRate.builder()
                                       .sourceCurrency(currencyFrom)
                                       .targetCurrency(currencyTo)
                                       .exchangeRate(exchangeRate)
                                       .timestamp(LocalDateTime.now()).build();

    log.info(String.format("Persisting new exchange rate %s -> %s",currencyFrom.getCode(),currencyTo.getCode()));
    return domainAdapter.fromEntityToValue(exchangeRateRepository.save(newRate));
  }

  public VExchangeRate getExchangeRate(String sourceCurrencyCode, String targetCurrencyCode) throws DomainException {
    log.info("Calculating exchange rate");
    return calculateExchangeRate(sourceCurrencyCode, targetCurrencyCode);
  }

  private VExchangeRate calculateExchangeRate(String sourceCurrencyCode, String targetCurrencyCode) throws DomainException {
    VCurrency currencyFrom = currencyDomainService.findOrCreateCurrency(sourceCurrencyCode);
    if (sourceCurrencyCode.equals(targetCurrencyCode)) {
      return buildVExchangeRate(currencyFrom,currencyFrom,BigDecimal.ONE);
    }

    VCurrency currencyTo = currencyDomainService.findOrCreateCurrency(targetCurrencyCode);

    if (sourceCurrencyCode.equals("EUR")) {
      Optional<ExchangeRate> exchangeRateOptional = exchangeRateRepository.findBySourceCurrencyIdAndTargetCurrencyId(currencyFrom.getId(), currencyTo.getId());
      BigDecimal exchangeRate = exchangeRateOptional
        .map(ExchangeRate::getExchangeRate)
        .orElseThrow(() -> new DomainException(EXCHANGE_RATE_NOT_FOUND.getCode(),
                                               String.format(EXCHANGE_RATE_NOT_FOUND.getMessage(), sourceCurrencyCode, targetCurrencyCode)));

      return buildVExchangeRate(currencyFrom, currencyTo, exchangeRate);
    }

    if (targetCurrencyCode.equals("EUR")) {
      Optional<ExchangeRate> exchangeRateOptional = exchangeRateRepository.findBySourceCurrencyIdAndTargetCurrencyId(currencyTo.getId(), currencyFrom.getId());

      BigDecimal exchangeRate = exchangeRateOptional
        .map(exchangeRateEntity -> {
          BigDecimal rate = exchangeRateEntity.getExchangeRate();
          return BigDecimal.ONE.divide(rate, 6, RoundingMode.HALF_UP);
        })
        .orElseThrow(() -> new DomainException(EXCHANGE_RATE_NOT_FOUND.getCode(),
                                               String.format(EXCHANGE_RATE_NOT_FOUND.getMessage(), sourceCurrencyCode, targetCurrencyCode)));

      return buildVExchangeRate(currencyFrom, currencyTo, exchangeRate);
    }

    Optional<ExchangeRate> exchangeRateFromSourceToEur = exchangeRateRepository.findBySourceCurrencyIdAndTargetCurrencyId(currencyDomainService.findOrCreateCurrency("EUR").getId(), currencyFrom.getId());
    Optional<ExchangeRate> exchangeRateEurToTarget = exchangeRateRepository.findBySourceCurrencyIdAndTargetCurrencyId(currencyDomainService.findOrCreateCurrency("EUR").getId(), currencyTo.getId());

    if (exchangeRateFromSourceToEur.isPresent() && exchangeRateEurToTarget.isPresent()) {
      BigDecimal sourceToEurRate = exchangeRateFromSourceToEur.get().getExchangeRate();
      BigDecimal eurToTargetRate = exchangeRateEurToTarget.get().getExchangeRate();

      BigDecimal amountInEUR = BigDecimal.ONE.divide(sourceToEurRate, 6, RoundingMode.HALF_UP);
      BigDecimal finalRate = amountInEUR.multiply(eurToTargetRate);

      return buildVExchangeRate(currencyFrom, currencyTo, finalRate);
    }

    throw new DomainException(EXCHANGE_RATE_NOT_FOUND.getCode(),
                              String.format(EXCHANGE_RATE_NOT_FOUND.getMessage(), sourceCurrencyCode, targetCurrencyCode));
  }

  private VExchangeRate buildVExchangeRate(VCurrency sourceCurrency, VCurrency targetCurrency, BigDecimal exchangeRate) {
    VExchangeRate vExchangeRate = new VExchangeRate();
    vExchangeRate.setSourceCurrency(sourceCurrency);
    vExchangeRate.setTargetCurrency(targetCurrency);
    vExchangeRate.setExchangeRate(exchangeRate);
    vExchangeRate.setTimestamp(LocalDateTime.now());
    return vExchangeRate;
  }
}
