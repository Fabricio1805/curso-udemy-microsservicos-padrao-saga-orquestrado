package br.com.microservices.orchestrated.paymentservice.domain.service;

import br.com.microservices.orchestrated.paymentservice.domain.entity.Payment;
import br.com.microservices.orchestrated.paymentservice.domain.repository.PaymentRepository;
import br.com.microservices.orchestrated.paymentservice.infrastructure.config.exception.ValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import br.com.microservices.orchestrated.paymentservice.application.dto.EventDTO;
import br.com.microservices.orchestrated.paymentservice.application.dto.HistoryDTO;
import br.com.microservices.orchestrated.paymentservice.application.dto.OrderProductDTO;
import br.com.microservices.orchestrated.paymentservice.application.kafka.producer.KafkaProducer;
import br.com.microservices.orchestrated.paymentservice.domain.enums.PaymentStatusEnum;
import br.com.microservices.orchestrated.paymentservice.domain.enums.SagaStatusEnum;
import br.com.microservices.orchestrated.paymentservice.infrastructure.utils.JsonUtil;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentService {
  private static final String CURRENT_SOURCE = "PAYMENT_SERVICE";
  private static final BigDecimal REDUCE_SUM_VALUE = BigDecimal.ZERO;
  private static final BigDecimal MIN_AMOUNT_VALUE = BigDecimal.valueOf(0.1);

  private final PaymentRepository paymentRepository;
  private final JsonUtil jsonUtil;
  private final KafkaProducer kafkaProducer;

  public void realizedPayment(EventDTO event) {
    try {
      checkCurrentValidation(event);
      createPendingPayment(event);

      Payment payment = findByOrderIdAndTransactionId(event);
      validateAmount(payment.getTotalAmount());
      changePaymentToSuccess(payment);

      handleSuccess(event);
    } catch (Exception exception) {
      log.error("Error trying to make payment", exception);
      handleFailCurrentNotExecuted(event, exception.getMessage());
    }

    kafkaProducer.sendEvent(jsonUtil.toJson(event));
  }

  private void checkCurrentValidation(EventDTO event) {
    if (paymentRepository.existsByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())) {
      throw new ValidationException("There's another transactionId for this validation");
    }
  }

  private void createPendingPayment(EventDTO event) {
    var totalAmount = calculateAmount(event);
    var totalItems = calculateTotalItems(event);
    var payment = Payment.builder()
        .orderId(event.getPayload().getId())
        .transactionId(event.getTransactionId())
        .totalAmount(totalAmount)
        .totalItems(totalItems)
        .build();

    save(payment);
  }

  private BigDecimal calculateAmount(EventDTO event) {
    return event.getPayload().getOrderProducts().stream()
        .map(product -> BigDecimal.valueOf(product.getProduct().getUnitValue())
                .multiply(BigDecimal.valueOf(product.getQuantity())))
        .reduce(REDUCE_SUM_VALUE, BigDecimal::add);
  }

  private int calculateTotalItems(EventDTO event) {
    return event.getPayload().getOrderProducts().stream().map(OrderProductDTO::getQuantity)
        .reduce(REDUCE_SUM_VALUE.intValue(), Integer::sum);
  }

  private Payment findByOrderIdAndTransactionId(EventDTO event) {
    return paymentRepository.findByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())
        .orElseThrow(() -> new ValidationException("Payment not found by OrderId and TransactionId"));
  }

  private void save(Payment payment) {
    paymentRepository.save(payment);
  }

  private void setEventAmountItems(EventDTO event, Payment payment) {
    event.getPayload().setTotalAmount(payment.getTotalAmount());
    event.getPayload().setTotalItems(payment.getTotalItems());
  }

  private void validateAmount(BigDecimal amount) {
    if (amount.compareTo(MIN_AMOUNT_VALUE) < 0) {
      throw new ValidationException("The minimum amount available is ".concat(MIN_AMOUNT_VALUE.toString()));
    }
  }

  private void changePaymentToSuccess(Payment payment) {
    payment.setStatus(PaymentStatusEnum.SUCCESS);
    save(payment);
  }

  private void handleSuccess(EventDTO event) {
    event.setStatus(SagaStatusEnum.SUCCESS);
    event.setSource(CURRENT_SOURCE);

    addHistory(event, "Payment realized successfully");
  }

  private void addHistory(EventDTO event, String message) {
    var history = HistoryDTO.builder()
        .source(event.getSource())
        .status(event.getStatus())
        .message(message)
        .createdAt(LocalDateTime.now())
        .build();

    event.addToHistory(history);
  }

  private void handleFailCurrentNotExecuted(EventDTO event, String message) {
    event.setStatus(SagaStatusEnum.ROLLBACK_PENDING);
    event.setSource(CURRENT_SOURCE);

    addHistory(event, "Fail to realized Payment: ".concat(message));
  }

  public void realizeRefund(EventDTO event) {
    event.setStatus(SagaStatusEnum.FAIL);
    event.setSource(CURRENT_SOURCE);
    try {
      changePaymentStatusToRefund(event);
      addHistory(event, "Rollback executed for Payment");
    } catch (Exception e) {
      addHistory(event, "Rollback not executed for Payment: ".concat(e.getMessage()));
    }

    kafkaProducer.sendEvent(jsonUtil.toJson(event));
  }

  private void changePaymentStatusToRefund(EventDTO event) {
    var payment = findByOrderIdAndTransactionId(event);
    payment.setStatus(PaymentStatusEnum.REFUND);
    setEventAmountItems(event, payment);

    save(payment);
  }

}
