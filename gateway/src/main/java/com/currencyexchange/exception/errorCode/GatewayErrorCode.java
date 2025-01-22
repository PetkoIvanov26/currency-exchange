package com.currencyexchange.exception.errorCode;

public enum GatewayErrorCode {
  CURRENCY_NOT_FOUND(2001, "Currency not found"),
  EXCHANGE_RATE_NOT_FOUND(2002, "Exchange rate not found");

  private final Integer code;
  private final String message;

  GatewayErrorCode(Integer code, String message) {
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
