package test.bcm.common.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@Slf4j
public class ObjectProducer implements IProducer<String, Object> { //FdaProducer

    private final KafkaTemplate<String, Object> template;

    public ObjectProducer(KafkaTemplate<String, Object> template) {
        this.template = template;
    }

    @Override
    public void sendEvent(String topic, String key, Object event, Headers headers) {
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(topic, null, key, event, headers);
        ListenableFuture<SendResult<String, Object>> future = template.send(producerRecord);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(@SuppressWarnings("NullableProblems") Throwable ex) {
                log.info("--- Unable to send event with key: {} and value: {} due to: {} ---", key, event.toString(), ex.getLocalizedMessage());
            }

            @Override
            public void onSuccess(SendResult<String, Object> result) {
                if (result != null) {
                    log.info("--- Sent event to topic: {} | partition: {} | offset: {} with key: {} and value: {} ---", topic,
                             result.getRecordMetadata().partition(), result.getRecordMetadata().offset(), key, event.toString());
                }
            }
        });
    }
}