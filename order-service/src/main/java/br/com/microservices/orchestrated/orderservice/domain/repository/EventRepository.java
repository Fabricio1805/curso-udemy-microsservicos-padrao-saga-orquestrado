package br.com.microservices.orchestrated.orderservice.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import br.com.microservices.orchestrated.orderservice.domain.document.EventDocument;

@Repository
public interface EventRepository extends MongoRepository<EventDocument, String> {
  
}
