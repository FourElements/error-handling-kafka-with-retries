package test.bcm.example.kafka.service;

import org.apache.kafka.common.header.Headers;
import test.bcm.common.model.BaseEvent;

public interface ProducerService {

    <T extends BaseEvent> void prepareAndSendEvent(String topic, String key, T receivedEvent);

    <T extends BaseEvent> void prepareAndSendEvent(String topic, String key, T receivedEvent, Headers headers);
}
