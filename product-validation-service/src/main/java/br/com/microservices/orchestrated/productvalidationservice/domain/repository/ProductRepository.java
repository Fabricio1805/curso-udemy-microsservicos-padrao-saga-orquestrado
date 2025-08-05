package br.com.microservices.orchestrated.productvalidationservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.microservices.orchestrated.productvalidationservice.domain.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
  Boolean existsByCode(String code);
}
