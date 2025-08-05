package br.com.microservices.orchestrated.paymentservice.application.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.microservices.orchestrated.paymentservice.infrastructure.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class PaymentConsumer {
  private final JsonUtil jsonUtil;

  @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.payment-success}"
  )
  public void consumerPaymentSuccessEvent(String payload) {
    log.info("Receiving success event {} from payment-success topic", payload);

    var event = jsonUtil.toEvent(payload);
    log.info(event.toString());
  }

    @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.payment-fail}"
  )
  public void consumerPaymentFailEvent(String payload) {
    log.info("Receiving rollback event {} from payment-fail topic", payload);

    var event = jsonUtil.toEvent(payload);
    log.info(event.toString());
  }
}
