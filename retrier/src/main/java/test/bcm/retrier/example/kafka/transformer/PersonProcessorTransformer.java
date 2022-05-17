package test.bcm.retrier.example.kafka.transformer;

import test.bcm.retrier.common.model.EventCounter;
import test.bcm.retrier.common.model.input.PersonInput;
import test.bcm.retrier.common.model.output.PersonOutput;
import test.bcm.retrier.example.constant.ExampleConstant;
import test.bcm.retrier.example.kafka.service.ProducerService;
import test.bcm.retrier.example.kafka.service.TransactionalProducerService;
import test.bcm.retrier.example.kafka.util.ExampleUtils;
import test.bcm.retrier.example.kafka.util.PersonValidator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;

import java.util.List;

@Slf4j
public class PersonProcessorTransformer implements Transformer<String, PersonInput, KeyValue<String, PersonOutput>> {

    private ProcessorContext context;

    private final PersonValidator validator;
    private final ProducerService retryService;
    private final ProducerService tombstoneService;
    private final ProducerService dlqService;
    private final TransactionalProducerService transactionalProducerService;
    private final InteractiveQueryService service;

    public PersonProcessorTransformer(PersonValidator validator, ProducerService retryService, ProducerService tombstoneService,
        TransactionalProducerService transactionalProducerService, ProducerService dlqService, InteractiveQueryService service) {
        this.validator = validator;
        this.retryService = retryService;
        this.tombstoneService = tombstoneService;
        this.transactionalProducerService = transactionalProducerService;
        this.service = service;
        this.dlqService = dlqService;
    }

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
    }

    @SneakyThrows
    @Override
    public KeyValue<String, PersonOutput> transform(String key, PersonInput value) {
        log.info("--- Attempt to reprocess input {} ---", value);

        log.info("--- This task has the following timestamp: {}", context.timestamp());

        ReadOnlyKeyValueStore<String, EventCounter> globalRetryCounterStore =
            service.getQueryableStore(ExampleConstant.PERSON_IN_RETRY_COUNTER_EVENT_STORE, QueryableStoreTypes.keyValueStore());

        Headers headers = context.headers();
        Header retryCounterHeader = ExampleUtils.searchHeaderByKey(headers, ExampleUtils.RETRY_COUNTER);
        EventCounter eventCounter = globalRetryCounterStore.get(value.getId());

        if (eventCounter != null && eventCounter.getCount() >= ExampleConstant.MAX_KEY_RETRIES) {
            dlqFlow(key, value, headers);
            return null; // do not forward the event down the pipeline
        }

        // before trying to process the event, is must validate the retry-counter header.
        // consult the current counter value from the GlobalKTable. if the event value is inferior, increment the counter and send back the event
        // to the topic (which effectively places it last). if the event value is equal, then it's in the proper order and can be processed
        if (retryCounterHeader == null && eventCounter != null || retryCounterHeader != null && eventCounter != null && ExampleUtils.byteArrayToInt(
            retryCounterHeader.value()) < eventCounter.getCount()) {
            retryFlow(value, headers, retryCounterHeader, eventCounter);
            return null; // do not forward the event down the pipeline
        }

        if (!validator.validateIfDeviceExistsInStateStore(value)) {
            retryFlowTransactional(value, headers);
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
        // event successfully processed so we need to send a tombstone record for redirect topic to warn of successful completion for that eventId
        tombstoneService.prepareAndSendEvent("bcm.test.queuing.person.in.redirect", value.getEventId(), value, headers);
        return KeyValue.pair(personOutput.getId(), personOutput);
    }

    private void retryFlowTransactional(PersonInput value, Headers headers) throws InterruptedException {
        log.info("--- Still no device was found! Update GlobalKTable counter and resend to the retry topic for a later attempt in a single "
                     + "transaction!");
        // Must send event to "GlobalKTable" first with the retry-counter updated. ex: "test-person-1" | eventCounter event (count incremented)
        // Not valid to process because no device exists in state store yet. Must send the event again to the retry topic to be later processed
        // ex: "test-person-1" | person event (with eventId, etc)
        log.info("--- Sleeping for 2 seconds before initiating transaction");
        Thread.sleep(2000);
        List<ProducerRecord<String, Object>> records = List.of(
            transactionalProducerService.prepareEventCounter("global-person-retry-counter", value.getId(), value, headers),
            transactionalProducerService.preparePersonInputEvent("bcm.test.queuing.person.in.retry", value.getId(), value, headers));
        transactionalProducerService.send(records);
    }

    private void retryFlow(PersonInput value, Headers headers, Header retryCounterHeader, EventCounter eventCounter) throws InterruptedException {
        log.info("--- Event arrived while the key was already in retries. Header counter: {} | Global counter: {}",
                 retryCounterHeader != null ? ExampleUtils.byteArrayToInt(retryCounterHeader.value()) : 0, eventCounter.getCount());
        log.info("--- Sleeping for 2 seconds before sending the event again back");
        Thread.sleep(2000);
        retryService.prepareAndSendEvent("bcm.test.queuing.person.in.retry", value.getId(), value, headers);
    }

    private void dlqFlow(String key, PersonInput value, Headers headers) {
        log.info("--- Reached MAX Retries for key: {}  Sending to DQL", value.getId());
        dlqService.prepareAndSendEvent("bcm.test.queuing.person.in.dlq", key, value, headers);

        // event was not successfully processed and reached max attempts. now its now retrier responsibility, but manual teams to verify
        // so send a tombstone record for redirect topic to clean that entry
        tombstoneService.prepareAndSendEvent("bcm.test.queuing.person.in.redirect", value.getEventId(), value, headers);
    }

    @Override
    public void close() {
        // NO-OP
    }
}
