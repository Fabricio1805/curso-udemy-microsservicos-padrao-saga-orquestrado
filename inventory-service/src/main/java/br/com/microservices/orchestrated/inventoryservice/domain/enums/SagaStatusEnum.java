package br.com.microservices.orchestrated.inventoryservice.domain.enums;

public enum SagaStatusEnum {
  SUCCESS,
  ROLLBACK_PENDING,
  FAIL;
}
