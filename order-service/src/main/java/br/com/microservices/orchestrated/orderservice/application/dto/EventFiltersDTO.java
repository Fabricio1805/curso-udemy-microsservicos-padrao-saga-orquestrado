package br.com.microservices.orchestrated.orderservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventFiltersDTO {
  private String orderId;
  private String transactionId;
}
