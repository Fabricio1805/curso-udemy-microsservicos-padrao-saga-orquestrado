package br.com.microservices.orchestrated.orchestratorservice.domain.enums;

public enum SagaStatusEnum {
  SUCCESS,
  ROLLBACK_PENDING,
  FAIL;
}
