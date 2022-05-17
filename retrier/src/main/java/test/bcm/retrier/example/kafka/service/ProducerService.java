package test.bcm.retrier.example.kafka.service;

import test.bcm.retrier.common.model.BaseEvent;
import org.apache.kafka.common.header.Headers;

public interface ProducerService {

    <T extends BaseEvent> void prepareAndSendEvent(String topic, String key, T receivedEvent, Headers headers);

    <T extends BaseEvent> void prepareAndSendEvent(String topic, String key, T receivedEvent, Headers headers, Integer retryCount);
}
