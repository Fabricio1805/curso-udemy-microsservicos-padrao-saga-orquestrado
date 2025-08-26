package br.com.microservices.orchestrated.orchestratorservice.application.saga;

import br.com.microservices.orchestrated.orchestratorservice.application.dto.EventDTO;
import br.com.microservices.orchestrated.orchestratorservice.domain.enums.TopicsEnum;
import br.com.microservices.orchestrated.orchestratorservice.infastructure.config.exception.ValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.Arrays;

@Slf4j
@Component
@AllArgsConstructor
public class SagaExecutionController {

    private static final String SAGA_LOG_ID = "ORDER ID: %s | TRANSACTION ID %s | EVENT ID %s ";
    public TopicsEnum getNextTopic(EventDTO event) {
        if (isEmpty(event.getSource()) || isEmpty(event.getStatus())) {
            throw new ValidationException("Source or Status must be informed");
        }

        var topic = findTopicBySourceAndStatus(event);
        logCurrentSaga(event, topic);
        return topic;
    }

    private TopicsEnum findTopicBySourceAndStatus(EventDTO event) {
        return (TopicsEnum) (Arrays.stream(SagaHandler.SAGA_HANDLER).filter(row -> isEventSourceAndStatusValid(event, row))
                .map(i -> i[SagaHandler.TOPIC_INDEX]).findFirst().orElseThrow(() -> new ValidationException("Topic not found")));
    }

    private boolean isEventSourceAndStatusValid(EventDTO event, Object[] row) {
        var source = row[SagaHandler.EVENT_SOURCE_INDEX];
        var status = row[SagaHandler.SAGA_STATUS_INDEX];
        return event.getSource().equals(source) && event.getStatus().equals(status);
    }

    private void logCurrentSaga(EventDTO event, TopicsEnum topic) {
        var sagaId = createSagaId(event);
        var source = event.getSource();

        switch (event.getStatus()) {
            case SUCCESS -> log.info("### CURRENT SAGA: {} | SUCESS | NEXT TOPIC {} | {}", source, topic, sagaId);
            case ROLLBACK_PENDING -> log.info("### CURRENT SAGA: {} | SENDING TO ROLLBACK CURRENT SERVICE | NEXT TOPIC {} | {}", source, topic, sagaId);
            case FAIL -> log.info("### CURRENT SAGA: {} | SENDING TO ROLLBACK PREVIOUS SERVICE | NEXT TOPIC {} | {}", source, topic, sagaId);
        }
    }

    private String createSagaId(EventDTO event) {
        return String.format(SAGA_LOG_ID, event.getPayload().getId(),
                event.getTransactionId(), event.getId());
    }
}
