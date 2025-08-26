package br.com.microservices.orchestrated.inventoryservice.domain.repository;

import java.util.List;

import br.com.microservices.orchestrated.inventoryservice.domain.entity.OrderInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderInventoryRepository extends JpaRepository<OrderInventory, Long>{
    Boolean existsByOrderIdAndTransactionId(String orderId, String transactionId);
    List<OrderInventory> findByOrderIdAndTransactionId(String orderId, String transactionId);  
}
