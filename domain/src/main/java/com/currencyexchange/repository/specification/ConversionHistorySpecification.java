package com.currencyexchange.repository.specification;

import com.currencyexchange.entity.CurrencyConversionHistory;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;

public class ConversionHistorySpecification {
  public static Specification<CurrencyConversionHistory> equalsTransactionId(String transactionId) {
    if (ObjectUtils.isEmpty(transactionId)) {
      return (root, cq, cb) -> cb.conjunction();
    } else {
      return (root, cq, cb) -> cb.equal(root.get("transactionId"), transactionId);
    }
  }

  public static Specification<CurrencyConversionHistory> equalsSourceCurrency(String sourceCurrencyCode) {
    if (ObjectUtils.isEmpty(sourceCurrencyCode)) {
      return (root, cq, cb) -> cb.conjunction();
    } else {
      return (root, cq, cb) -> cb.equal(root.get("sourceCurrency").get("code"), sourceCurrencyCode);
    }
  }

  public static Specification<CurrencyConversionHistory> equalsTargetCurrency(String targetCurrencyCode) {
    if (ObjectUtils.isEmpty(targetCurrencyCode)) {
      return (root, cq, cb) -> cb.conjunction();
    } else {
      return (root, cq, cb) -> cb.equal(root.get("targetCurrency").get("code"), targetCurrencyCode);
    }
  }

  public static Specification<CurrencyConversionHistory> filterFromDate(LocalDateTime fromDate) {
    if (ObjectUtils.isEmpty(fromDate)) {
      return (root, cq, cb) -> cb.conjunction();
    }
    return (root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("timestamp"), fromDate);
  }

  public static Specification<CurrencyConversionHistory> filterToDate(LocalDateTime toDate) {
    if (ObjectUtils.isEmpty(toDate)) {
      return (root, cq, cb) -> cb.conjunction();
    }
    return (root, cq, cb) -> cb.lessThanOrEqualTo(root.get("timestamp"), toDate);
  }

  public static Specification<CurrencyConversionHistory> filterBetweenDates(LocalDateTime fromDate, LocalDateTime toDate) {
    if (ObjectUtils.isEmpty(fromDate) && ObjectUtils.isEmpty(toDate)) {
      return (root, cq, cb) -> cb.conjunction();
    }
    return (root, cq, cb) -> {
      Predicate predicate = cb.conjunction();

      predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("timestamp"), fromDate));

      predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("timestamp"), toDate));

      return predicate;
    };
  }
}
