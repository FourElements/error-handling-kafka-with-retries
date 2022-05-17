package test.bcm.retrier.example.kafka.util;

import test.bcm.retrier.example.constant.ExampleConstant;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.List;

public class StoreUtils {

    private StoreUtils() {

    }

    @SuppressWarnings("unchecked")
    public static KeyValueStore<String, List<String>> retrieveRedirectPersonInMemoryStore(ProcessorContext context) {
        return (KeyValueStore<String, List<String>>) context.getStateStore(ExampleConstant.REDIRECT_PERSON_IN_MEMORY_EVENT_STORE);
    }
}
