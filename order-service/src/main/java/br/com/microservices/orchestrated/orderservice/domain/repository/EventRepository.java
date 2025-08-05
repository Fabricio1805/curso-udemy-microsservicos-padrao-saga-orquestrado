package br.com.microservices.orchestrated.orderservice.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import br.com.microservices.orchestrated.orderservice.domain.document.EventDocument;

@Repository
public interface EventRepository extends MongoRepository<EventDocument, String> {
  List<EventDocument> findAllByOrderByCreatedAtDesc();

  Optional<EventDocument> findTop1ByOrderIdByOrderByCreatedAtDesc(String orderId);

  Optional<EventDocument> findTop1ByTransactionIdByOrderByCreatedAtDesc(String transactionId);
}
