package br.com.microservices.orchestrated.paymentservice.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.microservices.orchestrated.paymentservice.domain.enums.PaymentStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment")
public class Payment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String orderId;

  @Column(nullable = false)
  private String transactionId;

  @Column(nullable = false)
  private int totalItems;

  @Column(nullable = false)
  private BigDecimal totalAmount;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private PaymentStatusEnum status;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  public void prePersist() {
    var now = LocalDateTime.now();
    createdAt = now;
    updatedAt = now;
    status = PaymentStatusEnum.PENDING;
  }

  @PreUpdate
  public void preUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
