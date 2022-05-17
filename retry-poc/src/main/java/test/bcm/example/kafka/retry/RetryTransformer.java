package test.bcm.example.kafka.retry;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.util.CollectionUtils;
import test.bcm.common.model.BaseEvent;
import test.bcm.example.kafka.service.IRetryRedirectFacade;
import test.bcm.example.util.StoreUtils;

import java.util.List;

@Slf4j
public class RetryTransformer<V extends BaseEvent> implements Transformer<String, V, KeyValue<String, V>> {

    private ProcessorContext context;

    private final IRetryRedirectFacade<String> facade;

    public RetryTransformer(IRetryRedirectFacade<String> facade) {
        this.facade = facade;
    }

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public KeyValue<String, V> transform(String key, V value) {
        log.info("--- Processing event: {} from topic: {}", value, context.topic());
        KeyValueStore<String, List<String>> store = StoreUtils.retrieveRedirectPersonInMemoryStore(context);
        log.info("--- Obtained state store: {} from RetryTransformer", store.name());
        String id = value.getId();
        List<String> eventIds = store.get(id);
        if (!CollectionUtils.isEmpty(eventIds)) {
            facade.execute(id, value, store, context.headers());
            return null;
        }
        log.info("--- No key: {} found in state store. Proceed with main flow! ", id);
        return KeyValue.pair(key, value);
    }

    @Override
    public void close() {
        // The Kafka Streams API will automatically close stores when necessary.
    }
}
