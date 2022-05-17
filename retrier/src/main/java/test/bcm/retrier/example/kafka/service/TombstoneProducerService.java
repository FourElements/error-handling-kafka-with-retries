package test.bcm.retrier.example.kafka.service;

import test.bcm.retrier.common.kafka.producer.IProducer;
import test.bcm.retrier.common.kafka.producer.StringProducer;
import test.bcm.retrier.common.model.BaseEvent;
import test.bcm.retrier.example.constant.ExampleConstant;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.stereotype.Service;

@Service
public class TombstoneProducerService implements ProducerService {

    private final IProducer<String, String> producer;

    public TombstoneProducerService(StringProducer producer) {this.producer = producer;}

    @Override
    public <T extends BaseEvent> void prepareAndSendEvent(String topic, String key, T receivedEvent, Headers headers) {
        Headers newHeaders = new RecordHeaders();
        newHeaders.add(ExampleConstant.ORIGIN_EVENT_KEY, receivedEvent.getId().getBytes());
        //headers.add(ExampleConstant.ORIGIN_EVENT_KEY, receivedEvent.getId().getBytes());
        producer.sendEvent(topic, key, null, newHeaders);
    }

    @Override
    public <T extends BaseEvent> void prepareAndSendEvent(String topic, String key, T receivedEvent, Headers headers, Integer retryCount) {
        Headers newHeaders = new RecordHeaders();
        newHeaders.add(ExampleConstant.ORIGIN_EVENT_KEY, receivedEvent.getId().getBytes());
        //headers.add(ExampleConstant.ORIGIN_EVENT_KEY, receivedEvent.getId().getBytes());
        producer.sendEvent(topic, key, null, newHeaders);
    }
}
