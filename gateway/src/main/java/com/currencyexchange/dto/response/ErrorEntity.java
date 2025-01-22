package com.currencyexchange.dto.response;

import lombok.Data;

@Data
public class ErrorEntity {
  private Integer errorCode;
  private String errorMessage;

  public ErrorEntity(Integer errorCode, String errorMessage) {
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }
}
