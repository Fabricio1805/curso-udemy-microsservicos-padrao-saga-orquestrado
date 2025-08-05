package br.com.microservices.orchestrated.orderservice.domain.service;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import br.com.microservices.orchestrated.orderservice.application.dto.EventFiltersDTO;
import br.com.microservices.orchestrated.orderservice.domain.document.EventDocument;
import br.com.microservices.orchestrated.orderservice.domain.repository.EventRepository;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class EventService {

  private final EventRepository eventRepository;

  public EventDocument save(EventDocument event) {
    return eventRepository.save(event);
  }

  public void notifyEnding(EventDocument event) {
    event.setOrderdId(event.getOrderdId());
    event.setCreatedAt(LocalDateTime.now());
    save(event);

    log.info("Order {} with saga Notified! TransactionId: {}", event.getOrderdId(), event.getTransactionId());
  }

  public List<EventDocument> findAll() {
    return eventRepository.findAllByOrderByCreatedAtDesc();
  }

  public EventDocument findByFilters(EventFiltersDTO filters) {
    validateEmptyFilters(filters);

    if (!ObjectUtils.isEmpty(filters.getOrderId())) {
      return findByOrderId(filters.getOrderId());
    }

    return findByTransactionId(filters.getTransactionId());

  }

  private EventDocument findByOrderId(String orderId) {
    return eventRepository.findTop1ByOrderIdByOrderByCreatedAtDesc(orderId)
        .orElseThrow(
            () -> new ValidationException("Event not found by orderId."));
  }

  private EventDocument findByTransactionId(String transactionId) {
    return eventRepository.findTop1ByTransactionIdByOrderByCreatedAtDesc(transactionId)
        .orElseThrow(() -> new ValidationException("Event not found by transactionId."));
  }

  private void validateEmptyFilters(EventFiltersDTO filters) {
    if (ObjectUtils.isEmpty(filters.getOrderId()) && ObjectUtils.isEmpty(filters.getTransactionId())) {
      throw new ValidationException("OrderId or TransactionId must be informed");
    }
  }
}
