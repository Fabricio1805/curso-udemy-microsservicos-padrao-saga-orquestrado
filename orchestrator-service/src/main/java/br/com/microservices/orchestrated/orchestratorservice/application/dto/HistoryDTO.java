package br.com.microservices.orchestrated.orchestratorservice.application.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;

import br.com.microservices.orchestrated.orchestratorservice.domain.enums.EventSourceEnum;
import br.com.microservices.orchestrated.orchestratorservice.domain.enums.SagaStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryDTO {
  private EventSourceEnum source;
  private SagaStatusEnum status;
  private LocalDateTime createdAt;
  private String message;

}
