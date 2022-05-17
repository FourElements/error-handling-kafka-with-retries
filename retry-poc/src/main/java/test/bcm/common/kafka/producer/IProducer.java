package test.bcm.common.kafka.producer;

import org.apache.kafka.common.header.Headers;

public interface IProducer<K, V> {

    void sendEvent(String topic, K key, V event, Headers headers);
}