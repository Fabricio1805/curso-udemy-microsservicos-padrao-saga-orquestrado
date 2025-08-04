package br.com.microservices.orchestrated.inventoryservice.application.dto;

import java.time.LocalDateTime;

import br.com.microservices.orchestrated.inventoryservice.domain.enums.SagaStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryDTO {
  private String source;
  private SagaStatusEnum status;
  private LocalDateTime createdAt;
  private String message;

}
