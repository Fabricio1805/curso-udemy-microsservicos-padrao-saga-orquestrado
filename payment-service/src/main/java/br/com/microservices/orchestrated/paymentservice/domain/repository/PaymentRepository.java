
package br.com.microservices.orchestrated.paymentservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.microservices.orchestrated.paymentservice.domain.entity.Payment;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
   Boolean existsByOrderIdAndTransactionId(String orderId, String transactionId);

   Optional<Payment> findByOrderIdAndTransactionId(String orderId, String transactionId);
}
