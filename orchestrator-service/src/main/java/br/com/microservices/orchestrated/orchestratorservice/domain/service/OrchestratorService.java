package br.com.microservices.orchestrated.orchestratorservice.domain.service;

import br.com.microservices.orchestrated.orchestratorservice.application.dto.EventDTO;
import br.com.microservices.orchestrated.orchestratorservice.application.dto.HistoryDTO;
import br.com.microservices.orchestrated.orchestratorservice.application.kafka.producer.SagaOrchestratorProducer;
import br.com.microservices.orchestrated.orchestratorservice.application.saga.SagaExecutionController;
import br.com.microservices.orchestrated.orchestratorservice.domain.enums.EventSourceEnum;
import br.com.microservices.orchestrated.orchestratorservice.domain.enums.SagaStatusEnum;
import br.com.microservices.orchestrated.orchestratorservice.domain.enums.TopicsEnum;
import br.com.microservices.orchestrated.orchestratorservice.infastructure.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class OrchestratorService {
    private final JsonUtil jsonUtil;
    private final SagaOrchestratorProducer sagaOrchestratorProducer;
    private final SagaExecutionController sagaExecutionController;

    public void startSaga(EventDTO event) {
        event.setSource(EventSourceEnum.ORCHESTRATOR);
        event.setStatus(SagaStatusEnum.SUCCESS);
        var topic = getTopic(event);
        log.info("SAGA STARTED");
        addHistory(event, "Saga started");
        sendToProducerWithTopic(event, topic);
    }

    public void finishSagaSuccess(EventDTO event) {
        event.setSource(EventSourceEnum.ORCHESTRATOR);
        event.setStatus(SagaStatusEnum.SUCCESS);
        log.info("SAGA FINISHED SUCCESSFULLY FOR EVENT {}", event.getId());
        addHistory(event, "Saga finished successfully");
        notifyFinishedSaga(event);
    }

    public void finishSagaFail(EventDTO event) {
        event.setSource(EventSourceEnum.ORCHESTRATOR);
        event.setStatus(SagaStatusEnum.FAIL);
        log.info("SAGA FINISHED WITH ERRORS FOR EVENT {}", event.getId());
        addHistory(event, "Saga finished with errors");
        notifyFinishedSaga(event);
    }

    public void continueSaga(EventDTO event) {
        var topic = getTopic(event);
        log.info("SAGA CONTINUING FOR EVENT {}", event.getId());
        sendToProducerWithTopic(event, topic);
    }

    private TopicsEnum getTopic (EventDTO event){
        return sagaExecutionController.getNextTopic(event);
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

    private void notifyFinishedSaga(EventDTO event) {
        sagaOrchestratorProducer.sendEvent(jsonUtil.toJson(event), TopicsEnum.NOTIFY_ENDING.getTopic());
    }

    private void sendToProducerWithTopic(EventDTO event,TopicsEnum topic){
        sagaOrchestratorProducer.sendEvent(jsonUtil.toJson(event), topic.getTopic());
    }
}
