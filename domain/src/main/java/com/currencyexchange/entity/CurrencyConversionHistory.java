package com.currencyexchange.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "currency_conversion")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyConversionHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "source_currency_id", nullable = false)
  private Currency sourceCurrency;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "target_currency_id", nullable = false)
  private Currency targetCurrency;

  @Column(name = "amount", precision = 19, scale = 4)
  private BigDecimal amount;

  @Column(name = "converted_amount", precision = 19, scale = 4)
  private BigDecimal convertedAmount;

  @Column(name = "transaction_id", length = 255)
  private String transactionId;

  @Column(name = "timestamp", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private LocalDateTime timestamp;
}