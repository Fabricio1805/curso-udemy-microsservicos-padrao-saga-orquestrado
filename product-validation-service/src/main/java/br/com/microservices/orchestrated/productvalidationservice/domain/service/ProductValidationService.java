package br.com.microservices.orchestrated.productvalidationservice.domain.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import br.com.microservices.orchestrated.productvalidationservice.application.dto.EventDTO;
import br.com.microservices.orchestrated.productvalidationservice.application.dto.HistoryDTO;
import br.com.microservices.orchestrated.productvalidationservice.application.dto.OrderProductDTO;
import br.com.microservices.orchestrated.productvalidationservice.application.kafka.producer.KafkaProducer;
import br.com.microservices.orchestrated.productvalidationservice.domain.entity.Validation;
import br.com.microservices.orchestrated.productvalidationservice.domain.enums.SagaStatusEnum;
import br.com.microservices.orchestrated.productvalidationservice.domain.repository.ProductRepository;
import br.com.microservices.orchestrated.productvalidationservice.domain.repository.ValidationRepository;
import br.com.microservices.orchestrated.productvalidationservice.infrastructure.utils.JsonUtil;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ProductValidationService {
  private static final String CURRENT_SOURCE = "PRODUCT_VALIDATION_SERVICE";

  private final ProductRepository productRepository;
  private final ValidationRepository validationRepository;
  private final JsonUtil jsonUtil;
  private final KafkaProducer kafkaProducer;


  public void validateExistingProducts(EventDTO event) {
    try {
      checkCurrentValidation(event);

      createValidation(event, true);

      handleSuccess(event);

    } catch (Exception exception) {
      log.error("Error trying to validate products", exception);
      handleFailCurrentNotExecuted(event, exception.getMessage());
    }

    kafkaProducer.sendEvent(jsonUtil.toJson(event));
  }

  private void checkCurrentValidation(EventDTO event) {
    validateProductsInformed(event);

    if (validationRepository.existsByOrderIdAndTransactionId(event.getOrderdId(), event.getTransactionId())) {
      throw new ValidationException("There's another transactionId for this validation");
    }

    event.getPayload().getOrderProducts().forEach(product -> {
      validateProductInformed(product);
      validateExistingProduct(product.getProduct().getCode());
    });

  }
  
  private void validateProductInformed(OrderProductDTO orderProduct) {
    if (ObjectUtils.isEmpty(orderProduct.getProduct()) || ObjectUtils.isEmpty(orderProduct.getProduct().getCode())) {
      throw new ValidationException("Product must be informed");
    }
  }

  private void validateProductsInformed(EventDTO event) {
    if (ObjectUtils.isEmpty(event.getPayload()) || ObjectUtils.isEmpty(event.getPayload().getOrderProducts())) {
      throw new ValidationException("Product List is Empty");
    }

    if (ObjectUtils.isEmpty(event.getPayload().getId()) || ObjectUtils.isEmpty(event.getPayload().getTransactionId())) {
      throw new ValidationException("OrderId and TransactionId must be informed");
    }
  }

  private void validateExistingProduct(String code) {
    if (!productRepository.existsByCode(code)) {
      throw new ValidationException("Product does not exists in database");
    }
  }


  private void createValidation(EventDTO event, boolean success) {
    var validation = Validation.builder()
        .orderId(event.getPayload().getId())
        .transactionId(event.getTransactionId())
        .success(success)
        .build();

    validationRepository.save(validation);
  }
  
  private void handleSuccess(EventDTO event) {
    event.setStatus(SagaStatusEnum.SUCCESS);
    event.setSource(CURRENT_SOURCE);

    addHistory(event, "Products are validated successfully");
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
    
  }
}

