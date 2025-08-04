package br.com.microservices.orchestrated.paymentservice.infrastructure.config.exception;

public record ExceptionDetail(int statusCode, String message) {
  
}
