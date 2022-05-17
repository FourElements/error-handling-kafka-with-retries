package test.bcm.example.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.stereotype.Service;
import test.bcm.common.kafka.producer.IProducer;
import test.bcm.common.kafka.producer.ObjectProducer;
import test.bcm.common.model.BaseEvent;

@Service
@Slf4j
public class RetryProducerService implements ProducerService {

    private final IProducer<String, Object> producer;

    public RetryProducerService(ObjectProducer producer) {
        this.producer = producer;
    }

    @Override
    public <T extends BaseEvent> void prepareAndSendEvent(String topic, String key, T receivedEvent) {
        producer.sendEvent(topic, key, receivedEvent, null);
    }

    @Override
    public <T extends BaseEvent> void prepareAndSendEvent(String topic, String key, T receivedEvent, Headers headers) {
        producer.sendEvent(topic, key, receivedEvent, headers);
    }
}
