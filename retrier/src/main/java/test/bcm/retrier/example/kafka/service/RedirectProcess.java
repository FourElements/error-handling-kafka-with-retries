package test.bcm.retrier.example.kafka.service;

import test.bcm.retrier.example.constant.ExampleConstant;
import test.bcm.retrier.example.kafka.util.ExampleUtils;
import test.bcm.retrier.example.kafka.util.StoreUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Slf4j
public class RedirectProcess implements Processor<String, String> {

    private ProcessorContext context;

    private final ProducerService tombstoneObjectService;

    public RedirectProcess(ProducerService tombstoneObjectService) {
        this.tombstoneObjectService = tombstoneObjectService;
    }

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public void process(String key, String value) {
        log.info("--- Processing event: {} with key: {} from topic: {}", value, key, context.topic());

        if (key == null && value == null) {
            log.warn("--- Nothing to process as key and value are null!");
            return;
        }

        KeyValueStore<String, List<String>> store = StoreUtils.retrieveRedirectPersonInMemoryStore(context);
        log.info("--- Obtained state store: {} from RedirectProcess", store.name());

        if (value == null) {
            processTombstoneRecord(key, store);
            return;
        }
        loadingStore(key, value, store);
    }

    /**
     * Process and load non-tombstone events.
     * key is eventId and value is the id of the event. ex: "015efb16-cadf-11ec-9d64-0242ac120002" | "test-person-1".
     * Use kafka header, origin-event-key, to search and obtain it's original key
     * Store contains for key, the id of the event, and for value, a list of eventIds.
     * Ex: "test-person-1" | ["015efb16-cadf-11ec-9d64-0242ac120002", "015efb16-cadf-11ec-9d64-0242ac1205432", etc.. ]
     *
     * @param key   event key, in this case eventId
     * @param value id of the event (it's origin key)
     * @param store previously loaded store, redirect-person in memory store
     */
    private void loadingStore(String key, String value, KeyValueStore<String, List<String>> store) {
        List<String> eventIds = store.get(value);
        if (eventIds == null) {
            log.info("--- Initialized key: {} with the eventId: {}", value, key);
            store.put(value, Collections.singletonList(key));
        } else {
            // Do not insert duplicates
            if (!eventIds.contains(key)) {
                eventIds.add(key);
                log.info("--- Added to existing key: {} the following eventId: {}", value, key);
                store.put(value, eventIds);
                return;
            }
            log.info("--- Store already contains eventId: {} for key: {}", value, key);
        }
    }

    /**
     * For a tombstone record: ex: "015efb16-cadf-11ec-9d64-0242ac120002" | null - meaning the event with id
     * "015efb16-cadf-11ec-9d64-0242ac120002" was processed successfully, therefore remove from the store.
     * Use kafka header, origin-event-key, to search and obtain it's original key
     * Store contains for key, the id of the event, and for value, a list of eventIds.
     * Ex: "test-person-1" | ["015efb16-cadf-11ec-9d64-0242ac120002", "015efb16-cadf-11ec-9d64-0242ac1205432", etc.. ]
     *
     * @param key   event key, in this case eventId
     * @param store previously loaded store, redirect-person in memory store
     */
    private void processTombstoneRecord(String key, KeyValueStore<String, List<String>> store) {
        Header header = ExampleUtils.searchHeaderByKey(context.headers(), ExampleConstant.ORIGIN_EVENT_KEY);
        if (header == null) {
            log.warn("--- Header with key: {} should exist! So this should never occur", ExampleConstant.ORIGIN_EVENT_KEY);
            return;
        }
        String originEventKey = new String(header.value(), StandardCharsets.UTF_8);
        List<String> eventIds = store.get(originEventKey);
        if (CollectionUtils.isEmpty(eventIds)) {
            log.info("--- Nothing to remove. No eventIds present in the list");
            return;
        }
        log.info("--- Event with id: {} was processed successfully in Retrier!. Removing eventId from the in memory store!", key);
        eventIds.remove(key);
        if (eventIds.isEmpty()) {
            log.info("--- All events for key: {} were processed successfully. Removing key from the in memory store!", originEventKey);
            store.delete(originEventKey);
            log.info("--- Removing that same key: {} in global retry counter store by sending a tombstone event!", originEventKey);
            tombstoneObjectService.prepareAndSendEvent("global-person-retry-counter", originEventKey, null, null);
        } else {
            log.info("--- Sending updated events id list: {} to the store: {}", eventIds, ExampleConstant.REDIRECT_PERSON_IN_MEMORY_EVENT_STORE);
            store.put(originEventKey, eventIds);
        }
    }

    @Override
    public void close() {
        // NO-OP
    }
}
