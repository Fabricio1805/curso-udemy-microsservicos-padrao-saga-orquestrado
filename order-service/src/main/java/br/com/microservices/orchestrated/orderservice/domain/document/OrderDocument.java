package br.com.microservices.orchestrated.orderservice.domain.document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "order")
public class OrderDocument {
  @Id
  private String id;
  private List<OrderProductDocument> orderProductDocuments;
  private LocalDateTime createdAt;
  private String transactionId;
  private BigDecimal totalAmount;
  private int totalItems;
}
