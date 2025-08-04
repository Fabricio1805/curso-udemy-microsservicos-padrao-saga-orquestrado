package br.com.microservices.orchestrated.paymentservice.domain.enums;

public enum SagaStatusEnum {
  SUCCESS,
  ROLLBACK_PENDING,
  FAIL;
}
