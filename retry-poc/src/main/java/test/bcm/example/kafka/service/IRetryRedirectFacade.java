package test.bcm.example.kafka.service;

import org.apache.kafka.common.header.Headers;
import org.apache.kafka.streams.state.KeyValueStore;
import test.bcm.common.model.BaseEvent;

import java.util.List;

public interface IRetryRedirectFacade<K> {

    <V extends BaseEvent> void execute(K key, V value, KeyValueStore<K, List<K>> store, Headers headers);
}
