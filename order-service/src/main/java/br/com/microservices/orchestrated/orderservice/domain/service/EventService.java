package br.com.microservices.orchestrated.orderservice.domain.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import br.com.microservices.orchestrated.orderservice.domain.document.EventDocument;
import br.com.microservices.orchestrated.orderservice.domain.repository.EventRepository;
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
}
