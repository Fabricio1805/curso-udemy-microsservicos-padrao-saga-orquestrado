package br.com.microservices.orchestrated.productvalidationservice.application.dto;

import java.time.LocalDateTime;
import java.util.List;

import br.com.microservices.orchestrated.productvalidationservice.domain.enums.SagaStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
  private String id;
  private String orderdId;
  private String transactionId;
  private OrderDTO payload;
  private String source;
  private SagaStatusEnum status;
  private List<HistoryDTO> eventHistory;
  private LocalDateTime createdAt;
}
