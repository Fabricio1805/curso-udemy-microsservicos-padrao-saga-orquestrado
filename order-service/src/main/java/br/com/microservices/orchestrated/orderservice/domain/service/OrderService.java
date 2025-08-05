package br.com.microservices.orchestrated.orderservice.domain.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.microservices.orchestrated.orderservice.application.dto.OrderRequestDTO;
import br.com.microservices.orchestrated.orderservice.application.kafka.producer.SagaProducer;
import br.com.microservices.orchestrated.orderservice.domain.document.EventDocument;
import br.com.microservices.orchestrated.orderservice.domain.document.OrderDocument;
import br.com.microservices.orchestrated.orderservice.domain.repository.OrderRepository;
import br.com.microservices.orchestrated.orderservice.infrastructure.utils.JsonUtil;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderService {

  private static final String TRANSACTION_ID_PATTNERN = "%s_%s";
  
  private final OrderRepository orderRepository;
  private final JsonUtil jsonUtil;
  private final EventService eventService;
  private final SagaProducer sagaProducer;


  public OrderDocument createOrder(OrderRequestDTO orderRequestDTO) {
    OrderDocument order = OrderDocument.builder()
        .orderProducts(orderRequestDTO.getProducts())
        .createdAt(LocalDateTime.now())
        .transactionId(String.format(TRANSACTION_ID_PATTNERN, Instant.now().toEpochMilli(), UUID.randomUUID()))
        .build();

    orderRepository.save(order);

    sagaProducer.sendEvent(jsonUtil.toJson(createPayload(order)));

    return order;
  }

  private EventDocument createPayload(OrderDocument order) {
    var event = EventDocument.builder()
      .orderdId(order.getId())
      .transactionId(order.getTransactionId())
      .createdAt(LocalDateTime.now())
      .payload(order)
        .build();

    return eventService.save(event);
  }

}
