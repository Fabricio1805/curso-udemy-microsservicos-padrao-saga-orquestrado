package br.com.microservices.orchestrated.orderservice.domain.document;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "event")
public class EventDocument {

  @Id
  private String id;
  private String orderdId;
  private String transactionId;
  private OrderDocument payload;
  private String source;
  private String status;
  private List<HistoryDocument> eventHistory;
  private LocalDateTime createdAt;
}
