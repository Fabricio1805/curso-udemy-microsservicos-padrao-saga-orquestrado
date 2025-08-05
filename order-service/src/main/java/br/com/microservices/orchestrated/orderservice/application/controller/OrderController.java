package br.com.microservices.orchestrated.orderservice.application.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.microservices.orchestrated.orderservice.application.dto.OrderRequestDTO;
import br.com.microservices.orchestrated.orderservice.domain.document.OrderDocument;
import br.com.microservices.orchestrated.orderservice.domain.service.OrderService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/order")
public class OrderController {
  private final OrderService orderService;


  @PostMapping
  public OrderDocument createOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
    return orderService.createOrder(orderRequestDTO);
  } 
}
