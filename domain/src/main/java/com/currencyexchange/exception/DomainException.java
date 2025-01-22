package com.currencyexchange.exception;

import com.currencyexchange.exception.errorCode.DomainErrorCode;

public class DomainException extends Exception {
  private Integer code;

  public DomainException(DomainErrorCode errorCode) {
    super(errorCode.getMessage());
    this.code = errorCode.getCode();
  }
  public DomainException(Integer code,String message ) {
    super(message);
    this.code = code;
  }
}
