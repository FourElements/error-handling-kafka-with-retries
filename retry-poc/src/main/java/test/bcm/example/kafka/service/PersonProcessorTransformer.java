package test.bcm.example.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import test.bcm.common.model.input.PersonInput;
import test.bcm.common.model.output.PersonOutput;
import test.bcm.example.util.PersonValidator;
import test.bcm.example.util.StoreUtils;

import java.util.List;

@Slf4j
public class PersonProcessorTransformer implements Transformer<String, PersonInput, KeyValue<String, PersonOutput>> {

    private ProcessorContext context;

    private final PersonValidator validator;
    private final IRetryRedirectFacade<String> facade;

    public PersonProcessorTransformer(PersonValidator validator, IRetryRedirectFacade<String> facade) {
        this.validator = validator;
        this.facade = facade;
    }

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public KeyValue<String, PersonOutput> transform(String key, PersonInput value) {
        KeyValueStore<String, List<String>> store = StoreUtils.retrieveRedirectPersonInMemoryStore(context);
        log.info("--- Obtained state store: {} from PersonProcessorTransformer", store.name());

        log.info("--- Executing main flow! Attempt to process input {} ---", value);

        if (!validator.validateIfDeviceExistsInStateStore(value)) {
            log.info("--- No device was found! Send to in-memory state store and for retry and redirect topics");
            facade.execute(key, value, store, context.headers());
            return null; // do not forward the event down the pipeline
        }

        // Device exists! Can properly process event
        PersonOutput personOutput = PersonOutput.builder()
                                                .eventId(value.getEventId())
                                                .id(value.getId())
                                                .name(value.getName())
                                                .job(value.getJob())
                                                .deviceId(value.getDeviceId())
                                                .build();
        log.info("Output: " + personOutput);
        return KeyValue.pair(personOutput.getId(), personOutput);
    }

    @Override
    public void close() {
        // NO-OP
    }
}
