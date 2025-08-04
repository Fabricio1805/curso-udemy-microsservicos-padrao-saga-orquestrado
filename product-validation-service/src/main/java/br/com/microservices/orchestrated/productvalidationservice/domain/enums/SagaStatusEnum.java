package br.com.microservices.orchestrated.productvalidationservice.domain.enums;

public enum SagaStatusEnum {
  SUCCESS,
  ROLLBACK_PENDING,
  FAIL;
}
