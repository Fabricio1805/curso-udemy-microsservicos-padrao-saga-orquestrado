package br.com.microservices.orchestrated.orderservice.application.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.microservices.orchestrated.orderservice.application.dto.EventFiltersDTO;
import br.com.microservices.orchestrated.orderservice.domain.document.EventDocument;
import br.com.microservices.orchestrated.orderservice.domain.service.EventService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/event")
public class EventController {
  private final EventService eventService;

  public EventDocument findByFilters(EventFiltersDTO filters) {
    return eventService.findByFilters(filters);
  }
  
  @GetMapping("/all")
  public List<EventDocument> findAll() {
    return eventService.findAll();
  }
}
