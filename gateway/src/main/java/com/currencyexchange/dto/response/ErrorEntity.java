package com.currencyexchange.dto.response;

import lombok.Data;

@Data
public class ErrorEntity {
  private String errorCode;
  private String errorMessage;

  public ErrorEntity(String errorCode, String errorMessage) {
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }
}
