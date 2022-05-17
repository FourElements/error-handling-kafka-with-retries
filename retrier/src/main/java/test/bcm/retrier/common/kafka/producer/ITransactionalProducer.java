package test.bcm.retrier.common.kafka.producer;

import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.List;

public interface ITransactionalProducer<K, V> {

    void sendEvents(List<ProducerRecord<K, V>> records);
}