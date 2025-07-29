package br.com.microservices.orchestrated.orchestratorservice.infastructure.config.kafka.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TopicsEnum {

  START_SAGA("start-saga"),
  BASE_ORCHESTRATOR("orchestrator"),
  FINISH_SUCCESS("finish_success"),
  FINISH_FAIL("finish-fail"),
  PRODUCT_VALIDATION_SUCCESS("product-validation-success"),
  PRODUCT_VALIDATION_FAIL("product-validation-fail"),
  PAYMENT_SUCCESS("payment-success"),
  PAYMENT_FAIL("paymen-fail"),
  INVENTORY_SUCCESS("inventory-success"),
  INVENTORY_FAIL("inventory-fail"),
  NOTIFY_ENDING("notify-ending");

  private String topic;
  
}
