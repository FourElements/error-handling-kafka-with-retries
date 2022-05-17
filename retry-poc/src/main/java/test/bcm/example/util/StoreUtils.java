package test.bcm.example.util;

import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import test.bcm.example.constant.ExampleConstant;

import java.util.List;

public class StoreUtils {

    private StoreUtils() {

    }

    @SuppressWarnings("unchecked")
    public static KeyValueStore<String, List<String>> retrieveRedirectPersonInMemoryStore(ProcessorContext context) {
        return (KeyValueStore<String, List<String>>) context.getStateStore(ExampleConstant.REDIRECT_PERSON_IN_MEMORY_EVENT_STORE);
    }
}
