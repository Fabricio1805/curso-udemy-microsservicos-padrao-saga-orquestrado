package br.com.microservices.orchestrated.inventoryservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductDTO {
  private ProductDTO productDocument;
  private int quantity;
}
