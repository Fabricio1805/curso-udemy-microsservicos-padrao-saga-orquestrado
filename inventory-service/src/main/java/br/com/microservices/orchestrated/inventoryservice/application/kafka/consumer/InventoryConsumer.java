package br.com.microservices.orchestrated.inventoryservice.application.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.microservices.orchestrated.inventoryservice.infrastructure.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import br.com.microservices.orchestrated.inventoryservice.domain.service.InventoryService;

@Slf4j
@Component
@AllArgsConstructor
public class InventoryConsumer {
  private final JsonUtil jsonUtil;
  private final InventoryService inventoryService;


  @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.inventory-success}"
  )
  public void consumerInventorySuccessEvent(String payload) {
    log.info("Receiving success event {} from inventory-success topic", payload);

    var event = jsonUtil.toEvent(payload);
    inventoryService.updateInventory(event);
  }

    @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.inventory-fail}"
  )
  public void consumerInventoryFailEvent(String payload) {
    log.info("Receiving rollback event {} from inventory-fail topic", payload);

    var event = jsonUtil.toEvent(payload);
    inventoryService.rollbackInventory(event);
  }
}
