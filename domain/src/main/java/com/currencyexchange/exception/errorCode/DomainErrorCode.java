package com.currencyexchange.exception.errorCode;

public enum DomainErrorCode {
  CURRENCY_NOT_FOUND(1001, "Currency not found"),
  EXCHANGE_RATE_NOT_FOUND(1002, "Exchange rate not found for %s to %s");

  private final Integer code;
  private final String message;

  DomainErrorCode(Integer code, String message) {
    this.code = code;
    this.message = message;
  }

  public Integer getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}
