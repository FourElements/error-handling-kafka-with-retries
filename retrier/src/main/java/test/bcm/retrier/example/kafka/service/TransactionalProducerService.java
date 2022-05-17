package test.bcm.retrier.example.kafka.service;

import test.bcm.retrier.common.kafka.producer.TransactionalObjectProducer;
import test.bcm.retrier.common.model.BaseEvent;
import test.bcm.retrier.common.model.EventCounter;
import test.bcm.retrier.example.kafka.util.ExampleUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TransactionalProducerService extends ATransactionalProducerService {

    public TransactionalProducerService(TransactionalObjectProducer producer) {
        super(producer);
    }

    public <T extends BaseEvent> ProducerRecord<String, Object> prepareEventCounter(String topic, String key, T receivedEvent, Headers headers) {

        Header header = ExampleUtils.searchHeaderByKey(headers, ExampleUtils.RETRY_COUNTER);
        int count = 0;
        if (header == null) {
            count += 1;
        } else {
            int currentCount = ExampleUtils.byteArrayToInt(header.value());
            count = currentCount + 1;
        }
        EventCounter eventCounter = EventCounter.builder()
                                                .id(receivedEvent.getId())
                                                .eventId(receivedEvent.getEventId())
                                                .count(count)
                                                .build();
        return super.prepare(topic, key, eventCounter, null);
    }

    public <T extends BaseEvent> ProducerRecord<String, Object> preparePersonInputEvent(String topic, String key, T receivedEvent, Headers headers) {

        Header header = ExampleUtils.searchHeaderByKey(headers, ExampleUtils.RETRY_COUNTER);
        if (header == null) {
            header = ExampleUtils.createRetryCountHeader(1);
        } else {
            header = ExampleUtils.createRetryCountHeaderAndIncrementValue(header);
        }
        Headers newHeaders = new RecordHeaders();
        newHeaders.add(header);
        return super.prepare(topic, key, receivedEvent, newHeaders);
    }
}
