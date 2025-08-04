package br.com.microservices.orchestrated.paymentservice.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
  private String id;
  private List<OrderProductDTO> orderProductDocuments;
  private LocalDateTime createdAt;
  private String transactionId;
  private BigDecimal totalAmount;
  private int totalItems;
}
