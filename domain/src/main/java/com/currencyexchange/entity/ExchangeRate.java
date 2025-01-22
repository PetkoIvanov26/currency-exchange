package com.currencyexchange.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "exchange_rate")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRate {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "source_currency_id")
  private Currency sourceCurrency;

  @ManyToOne
  @JoinColumn(name = "target_currency_id")
  private Currency targetCurrency;

  @Column(name = "exchange_rate", nullable = false, precision = 15, scale = 6)
  private BigDecimal exchangeRate;

  @Column(name = "timestamp", nullable = false)
  private LocalDateTime timestamp;
}
