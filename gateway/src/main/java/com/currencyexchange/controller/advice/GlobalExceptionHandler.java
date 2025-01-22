package com.currencyexchange.controller.advice;

import com.currencyexchange.dto.response.ErrorEntity;
import com.currencyexchange.exception.GatewayException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(GatewayException.class)
  public ResponseEntity<ErrorEntity> handleGatewayException(GatewayException ex) {
    ErrorEntity errorEntity = new ErrorEntity(ex.getCode(), ex.getMessage());
    return new ResponseEntity<>(errorEntity, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorEntity> handleGeneralException(Exception ex) {
    ErrorEntity errorEntity = new ErrorEntity(0001, ex.getMessage());
    return new ResponseEntity<>(errorEntity, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
