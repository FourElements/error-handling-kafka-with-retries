package test.bcm.retrier.example.kafka.service;

import test.bcm.retrier.common.kafka.producer.IProducer;
import test.bcm.retrier.common.kafka.producer.ObjectProducer;
import test.bcm.retrier.common.model.BaseEvent;
import org.apache.kafka.common.header.Headers;
import org.springframework.stereotype.Service;

@Service
public class TombstoneObjectProducerService implements ProducerService {

    private final IProducer<String, Object> producer;

    public TombstoneObjectProducerService(ObjectProducer producer) {this.producer = producer;}

    @Override
    public <T extends BaseEvent> void prepareAndSendEvent(String topic, String key, T receivedEvent, Headers headers) {
        producer.sendEvent(topic, key, null, null);
    }

    @Override
    public <T extends BaseEvent> void prepareAndSendEvent(String topic, String key, T receivedEvent, Headers headers, Integer retryCount) {
        producer.sendEvent(topic, key, null, null);
    }
}
