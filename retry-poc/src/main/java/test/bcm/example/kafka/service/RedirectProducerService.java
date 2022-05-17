package test.bcm.example.kafka.service;

import org.apache.kafka.common.header.Headers;
import org.springframework.stereotype.Service;
import test.bcm.common.kafka.producer.IProducer;
import test.bcm.common.kafka.producer.StringProducer;
import test.bcm.common.model.BaseEvent;
import test.bcm.example.constant.ExampleConstant;

@Service
public class RedirectProducerService implements ProducerService {

    private final IProducer<String, String> producer;

    public RedirectProducerService(StringProducer producer) {this.producer = producer;}

    @Override
    public <T extends BaseEvent> void prepareAndSendEvent(String topic, String key, T receivedEvent) {
        producer.sendEvent(topic, receivedEvent.getEventId(), key, null);
    }

    @Override
    public <T extends BaseEvent> void prepareAndSendEvent(String topic, String key, T receivedEvent, Headers headers) {
        headers.add(ExampleConstant.ORIGIN_EVENT_KEY, key.getBytes());
        producer.sendEvent(topic, receivedEvent.getEventId(), key, headers);
    }
}
