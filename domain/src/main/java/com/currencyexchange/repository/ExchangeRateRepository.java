package com.currencyexchange.repository;

import com.currencyexchange.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

  Optional<ExchangeRate> findBySourceCurrencyIdAndTargetCurrencyId(Long fromCurrencyId, Long toCurrencyId);
}
