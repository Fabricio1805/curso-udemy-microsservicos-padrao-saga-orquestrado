package br.com.microservices.orchestrated.inventoryservice.domain.service;

import br.com.microservices.orchestrated.inventoryservice.application.dto.EventDTO;
import br.com.microservices.orchestrated.inventoryservice.application.dto.HistoryDTO;
import br.com.microservices.orchestrated.inventoryservice.application.dto.OrderDTO;
import br.com.microservices.orchestrated.inventoryservice.application.dto.OrderProductDTO;
import br.com.microservices.orchestrated.inventoryservice.application.kafka.producer.KafkaProducer;
import br.com.microservices.orchestrated.inventoryservice.domain.entity.Inventory;
import br.com.microservices.orchestrated.inventoryservice.domain.entity.OrderInventory;
import br.com.microservices.orchestrated.inventoryservice.domain.enums.SagaStatusEnum;
import br.com.microservices.orchestrated.inventoryservice.domain.repository.InventoryRepository;
import br.com.microservices.orchestrated.inventoryservice.domain.repository.OrderInventoryRepository;
import br.com.microservices.orchestrated.inventoryservice.infrastructure.config.exception.ValidationException;
import br.com.microservices.orchestrated.inventoryservice.infrastructure.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class InventoryService {
  private static final String CURRENT_SOURCE = "INVENTORY_SERVICE";
  private final InventoryRepository inventoryRepository;
  private final JsonUtil jsonUtil;
  private final KafkaProducer kafkaProducer;
  private final OrderInventoryRepository orderInventoryRepository;

  public void updateInventory(EventDTO event) {
    try {
      checkCurrentValidation(event);
      createOrderInventory(event);
      updateInventory(event.getPayload());

      handleSuccess(event);
    } catch (Exception exception) {
      log.error("Error trying to update inventory", exception);
      handleFailCurrentNotExecuted(event, exception.getMessage());
    }

    kafkaProducer.sendEvent(jsonUtil.toJson(event));
  }

  private void checkCurrentValidation(EventDTO event) {
    if (orderInventoryRepository.existsByOrderIdAndTransactionId(event.getPayload().getId(),
        event.getTransactionId())) {
      throw new ValidationException("There's another transactionId for this validation");
    }
  }

  private void createOrderInventory(EventDTO event) {
    event.getPayload().getOrderProducts()
        .forEach(product -> {
          var inventory = findInventoryByProductCode(product.getProduct().getCode());
          OrderInventory orderInventory = createOrderInventory(event, product, inventory);
          orderInventoryRepository.save(orderInventory);

        });
  }

  private OrderInventory createOrderInventory(EventDTO event, OrderProductDTO product, Inventory inventory) {
    return OrderInventory.builder()
        .inventory(inventory)
        .orderQuantity(product.getQuantity())
        .oldQuantity(inventory.getAvailable())
        .newQuantity(inventory.getAvailable() - product.getQuantity())
        .orderId(event.getPayload().getId())
        .transactionId(event.getTransactionId())
        .build();
  }

  private void updateInventory(OrderDTO order) {
    order.getOrderProducts().forEach(product -> {
      var inventory = findInventoryByProductCode(product.getProduct().getCode());
      checkInventory(inventory.getAvailable(), product.getQuantity());
      inventory.setAvailable(inventory.getAvailable() - product.getQuantity());
      inventoryRepository.save(inventory);
    });
  }

  private void checkInventory(int available, int orderQuantity) {
    if (orderQuantity > available) {
      throw new ValidationException("Product is out of stock");
    }
  }

  private Inventory findInventoryByProductCode(String productCode) {
    return inventoryRepository.findByProductCode(productCode)
        .orElseThrow(() -> new ValidationException("Inventory not found by informed product"));
  }

  private void handleSuccess(EventDTO event) {
    event.setStatus(SagaStatusEnum.SUCCESS);
    event.setSource(CURRENT_SOURCE);

    addHistory(event, "Payment realized successfully");
  }

  private void addHistory(EventDTO event, String message) {
    var history = HistoryDTO.builder()
        .source(event.getSource())
        .status(event.getStatus())
        .message(message)
        .createdAt(LocalDateTime.now())
        .build();

    event.addToHistory(history);
  }

  private void handleFailCurrentNotExecuted(EventDTO event, String message) {
    event.setStatus(SagaStatusEnum.ROLLBACK_PENDING);
    event.setSource(CURRENT_SOURCE);

    addHistory(event, "Fail to update invertory: ".concat(message));
  }

  public void rollbackInventory(EventDTO event) {
    event.setStatus(SagaStatusEnum.FAIL);
    event.setSource(CURRENT_SOURCE);
    try {
      returnInventoryToPreviousValues(event);
      addHistory(event, "Rollback executed for inventory");
    } catch (Exception e) {
      addHistory(event, "Rollback not executed for inventory: ".concat(e.getMessage()));
    }

    kafkaProducer.sendEvent(jsonUtil.toJson(event));
  }

  private void returnInventoryToPreviousValues(EventDTO event) {
    orderInventoryRepository.findByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())
        .forEach(orderInventory -> {
          var inventory = orderInventory.getInventory();
          inventory.setAvailable(orderInventory.getOldQuantity());
          inventoryRepository.save(inventory);
          log.info("Restored inventory for order {} from {} to {}", event.getPayload().getId(),
              orderInventory.getNewQuantity(), inventory.getAvailable());
        });
  }

}
