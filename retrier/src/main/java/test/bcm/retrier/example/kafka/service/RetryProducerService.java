package test.bcm.retrier.example.kafka.service;

import test.bcm.retrier.common.kafka.producer.IProducer;
import test.bcm.retrier.common.kafka.producer.ObjectProducer;
import test.bcm.retrier.common.model.BaseEvent;
import test.bcm.retrier.example.kafka.util.ExampleUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RetryProducerService implements ProducerService {

    private final IProducer<String, Object> producer;

    public RetryProducerService(ObjectProducer producer) {
        this.producer = producer;
    }

    @Override
    public <T extends BaseEvent> void prepareAndSendEvent(String topic, String key, T receivedEvent, Headers headers) {
        Header newHeader;
        Header header = ExampleUtils.searchHeaderByKey(headers, ExampleUtils.RETRY_COUNTER);
        log.info("--- Keep calm and Keep retrying ---");
        if (header == null) {
            newHeader = ExampleUtils.createRetryCountHeader(1);
        } else {
            newHeader = ExampleUtils.createRetryCountHeaderAndIncrementValue(header);
        }
        Headers newHeaders = new RecordHeaders();
        newHeaders.add(newHeader);
        producer.sendEvent(topic, key, receivedEvent, newHeaders);
    }

    @Override
    public <T extends BaseEvent> void prepareAndSendEvent(String topic, String key, T receivedEvent, Headers headers, Integer retryCount) {
        log.info("--- Event arrived while the key was already in retries. Updating retry count to the current: {}", retryCount);
        Header newHeader = ExampleUtils.createRetryCountHeader(retryCount);
        Headers newHeaders = new RecordHeaders();
        newHeaders.add(newHeader);
        producer.sendEvent(topic, key, receivedEvent, newHeaders);
    }
}
