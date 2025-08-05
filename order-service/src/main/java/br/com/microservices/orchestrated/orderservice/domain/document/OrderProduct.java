package br.com.microservices.orchestrated.orderservice.domain.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderProduct {
  private Product product;
  private int quantity;
}
