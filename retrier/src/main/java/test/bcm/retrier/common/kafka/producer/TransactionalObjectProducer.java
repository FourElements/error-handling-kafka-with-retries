package test.bcm.retrier.common.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;

@Component
@Slf4j
public class TransactionalObjectProducer implements ITransactionalProducer<String, Object> {

    private final KafkaTemplate<String, Object> template;

    public TransactionalObjectProducer(@Qualifier("kafkaTemplateObjectWithTransaction") KafkaTemplate<String, Object> template) {
        this.template = template;
    }

    @Override
    public void sendEvents(List<ProducerRecord<String, Object>> records) {

        Boolean result = template.executeInTransaction(operations -> {
            records.forEach(r -> {
                String topic = r.topic();
                String key = r.key();
                Object event = r.value();
                ListenableFuture<SendResult<String, Object>> future = operations.send(r);
                future.addCallback(new ListenableFutureCallback<>() {
                    @Override
                    public void onFailure(@SuppressWarnings("NullableProblems") Throwable ex) {
                        log.info("--- Unable to send event with key: {} and value: {} due to: {} ---", key, event.toString(),
                                 ex.getLocalizedMessage());
                    }

                    @Override
                    public void onSuccess(SendResult<String, Object> result) {
                        if (result != null) {
                            log.info("--- Sent event to topic: {} | partition: {} | offset: {} with key: {} and value: {} ---", topic,
                                     result.getRecordMetadata().partition(), result.getRecordMetadata().offset(), key, event.toString());
                        }
                    }
                });
            });
            return true;
        });
        log.info("Did transaction complete successfully? {}", result);
    }
}