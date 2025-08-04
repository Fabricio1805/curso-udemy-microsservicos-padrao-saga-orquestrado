package br.com.microservices.orchestrated.orderservice.infrastructure.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.microservices.orchestrated.orderservice.domain.document.EventDocument;
import lombok.AllArgsConstructor;


@Component
@AllArgsConstructor
public class JsonUtil {
  private final ObjectMapper objectMapper;

    private final Logger LOG = LoggerFactory.getLogger(ObjectMapper.class);

  public String toJson(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (Exception e) {
      LOG.error("Erro ao converter para json: {}", e.getMessage());
      return "";
    }
  }

  public EventDocument toEvent(String json) {
     try {
      return objectMapper.readValue(json, EventDocument.class);
    } catch (Exception e) {
       LOG.error("Erro ao converter para EventDocument: {}", e.getMessage());
       return null;
    }
  }
}
