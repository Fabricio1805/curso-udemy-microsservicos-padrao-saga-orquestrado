package br.com.microservices.orchestrated.orderservice.domain.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductDocument {
  private ProductDocument productDocument;
  private int quantity;
}
