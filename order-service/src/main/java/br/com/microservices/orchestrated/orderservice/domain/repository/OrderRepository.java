package br.com.microservices.orchestrated.orderservice.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import br.com.microservices.orchestrated.orderservice.domain.document.OrderDocument;

@Repository
public interface OrderRepository extends MongoRepository<OrderDocument, String> {
  
}
