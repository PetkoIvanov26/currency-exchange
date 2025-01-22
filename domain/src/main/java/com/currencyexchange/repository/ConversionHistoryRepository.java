package com.currencyexchange.repository;

import com.currencyexchange.entity.CurrencyConversionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversionHistoryRepository extends JpaRepository<CurrencyConversionHistory, Long>,
  JpaSpecificationExecutor<CurrencyConversionHistory> {

}
