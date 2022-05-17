package test.bcm.retrier.example.kafka.service;

import test.bcm.retrier.common.kafka.producer.ITransactionalProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;

import java.util.List;

public abstract class ATransactionalProducerService {

    private final ITransactionalProducer<String, Object> producer;

    protected ATransactionalProducerService(ITransactionalProducer<String, Object> producer) {
        this.producer = producer;
    }

    ProducerRecord<String, Object> prepare(String topic, String key, Object event, Headers headers) {
        return new ProducerRecord<>(topic, null, key, event, headers);
    }

    public void send(List<ProducerRecord<String, Object>> records) {
        producer.sendEvents(records);
    }
}
