package test.bcm.example.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import test.bcm.common.model.BaseEvent;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class RetryRedirectFacade implements IRetryRedirectFacade<String> {

    private final ProducerService retryService;
    private final ProducerService redirectService;

    public RetryRedirectFacade(RetryProducerService retryService, RedirectProducerService redirectService) {
        this.retryService = retryService;
        this.redirectService = redirectService;
    }

    @Override
    public <V extends BaseEvent> void execute(String key, V value, KeyValueStore<String, List<String>> store, Headers headers) {
        String id = value.getId();
        List<String> eventIds = store.get(id);
        if (CollectionUtils.isEmpty(eventIds)) {
            store.put(id, Collections.singletonList((value.getEventId())));
            log.info("--- Starting to redirect the next events immediately! Added key: {} with the following eventId: {}", id, value.getEventId());
        } else {
            log.info("--- Key: {} is found in state store! Must continue to redirect events!", id);
            eventIds.add(value.getEventId());
            store.put(id, eventIds);
            log.info("--- Added to existing key: {} the following eventId: {}", value.getId(), value.getEventId());
        }
        // Not valid to process because no device exists in state store yet. Must send the event to the retry topic to be later processed
        // ex: "test-person-1" | person event (with eventId, etc)
        retryService.prepareAndSendEvent("bcm.test.queuing.person.in.retry", id, value);

        // Must also send an event to the redirect topic to keep track of redirected events.
        // The key will be swapped to eventId and the payload will just contain the key. Also send the key in the kafka header
        // ex: "015efb16-cadf-11ec-9d64-0242ac120002" | "test-person-1" | H: "origin-event-key": "test-person-1"
        redirectService.prepareAndSendEvent("bcm.test.queuing.person.in.redirect", id, value, headers);
    }
}
