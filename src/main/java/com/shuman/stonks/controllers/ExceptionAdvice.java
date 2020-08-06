package com.shuman.stonks.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionAdvice {
   @ExceptionHandler(value = Throwable.class)
   public ResponseEntity<Object> exception(Throwable exception) {
      return new ResponseEntity<>(exception.toString(), HttpStatus.BAD_REQUEST);
   }
}