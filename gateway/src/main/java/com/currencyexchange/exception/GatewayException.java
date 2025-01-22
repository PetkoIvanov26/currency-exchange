package com.currencyexchange.exception;

import com.currencyexchange.exception.errorCode.GatewayErrorCode;

public class GatewayException extends Exception{
  private Integer code;
  public GatewayException(GatewayErrorCode errorCode, Throwable cause){
    super(errorCode.getMessage(),cause);
    this.code = errorCode.getCode();
  }
}
