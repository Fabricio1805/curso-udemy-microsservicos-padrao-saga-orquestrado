package br.com.microservices.orchestrated.orderservice.application.dto;

import java.util.List;

import br.com.microservices.orchestrated.orderservice.domain.document.OrderProductDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {

  private List<OrderProductDocument> products;
}