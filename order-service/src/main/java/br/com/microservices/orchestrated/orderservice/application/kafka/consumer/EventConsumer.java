package br.com.microservices.orchestrated.orderservice.application.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.microservices.orchestrated.orderservice.infrastructure.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class EventConsumer {
  private final JsonUtil jsonUtil;

  @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.notify-ending}"
  )
  public void consumerNotifyEndingEvent(String payload) {
    log.info("Receiving ending notification event {} from notify-ending topic", payload);

    var event = jsonUtil.toEvent(payload);
    log.info(event.toString());
  }
}
