package test.bcm.retrier.common.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@Slf4j
public class StringProducer implements IProducer<String, String> {

    private final KafkaTemplate<String, String> template;

    public StringProducer(KafkaTemplate<String, String> template) {this.template = template;}

    @Override
    public void sendEvent(String topic, String key, String event, Headers headers) {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, null, key, event, headers);
        ListenableFuture<SendResult<String, String>> future = template.send(producerRecord);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(@SuppressWarnings("NullableProblems") Throwable ex) {
                log.info("--- Unable to send event with key: {} and value: {} due to: {} ---", key, event, ex.getLocalizedMessage());
            }

            @Override
            public void onSuccess(SendResult<String, String> result) {
                if (result != null) {
                    log.info("--- Sent event to topic: {} | partition: {} | offset: {} with key: {} and value: {} ---", topic,
                             result.getRecordMetadata().partition(), result.getRecordMetadata().offset(), key, event);
                }
            }
        });
    }
}
