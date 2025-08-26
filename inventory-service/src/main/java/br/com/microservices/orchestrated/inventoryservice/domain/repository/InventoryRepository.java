package br.com.microservices.orchestrated.inventoryservice.domain.repository;

import br.com.microservices.orchestrated.inventoryservice.domain.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long>{
    Optional<Inventory> findByProductCode(String productCode);
}
