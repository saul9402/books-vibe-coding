package com.mitocode.exception;

import com.mitocode.dto.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponse<CustomErrorResponse>> handleDefaultException(Exception ex, ServerWebExchange exchange) {
        CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(), exchange.getRequest().getPath().value());
        return new ResponseEntity<>(new GenericResponse<>(500, "failed", List.of(err)), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ModelNotFoundException.class)
    public ResponseEntity<GenericResponse<CustomErrorResponse>> handleModelNotFoundException(ModelNotFoundException ex, ServerWebExchange exchange) {
        CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(), exchange.getRequest().getPath().value());
        return new ResponseEntity<>(new GenericResponse<>(404, "not-found", List.of(err)), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomErrorResponse> handleAccessDeniedException(AccessDeniedException ex, ServerWebExchange exchange) {
        CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(), exchange.getRequest().getPath().value());
        return new ResponseEntity<>(err, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<GenericResponse<CustomErrorResponse>> handleBadRequest(WebExchangeBindException ex, ServerWebExchange exchange) {
        CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(), exchange.getRequest().getPath().value());
        return new ResponseEntity<>(new GenericResponse<>(400, "bad-request", List.of(err)), HttpStatus.BAD_REQUEST);
    }
}
