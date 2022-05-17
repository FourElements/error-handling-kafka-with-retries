package test.bcm.example.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.springframework.stereotype.Service;
import test.bcm.common.model.input.PersonInput;
import test.bcm.common.model.output.PersonOutput;
import test.bcm.example.util.PersonValidator;

@Service
@Slf4j
// NOT USED; REPLACED WITH PersonProcessorTransformer!
public class ProcessPersonServiceImpl implements ProcessPersonService {

    private final PersonValidator validator;
    private final ProducerService retryService;
    private final ProducerService redirectService;

    public ProcessPersonServiceImpl(PersonValidator validator, RetryProducerService retryService, RedirectProducerService redirectService) {
        this.validator = validator;
        this.retryService = retryService;
        this.redirectService = redirectService;
    }

    public KeyValue<String, PersonOutput> inputToOutput(String key, PersonInput personInput) {

        log.info("--- Attempt to process input {} ---", personInput);

        if (!validator.validateIfDeviceExistsInStateStore(personInput)) {
            log.info("--- No device was found! Send to in-memory state store and for retry and redirect topics");
            // Not valid to process because no device exists in state store yet. Must send the event to the retry topic to be later processed
            // ex: "test-person-1" | person event (with eventId, etc)
            retryService.prepareAndSendEvent("bcm.test.queuing.person.in.retry", personInput.getId(), personInput);

            // Must also send an event to the redirect topic to keep track of redirected events.
            // The key will be swapped to eventId and the payload will just contain the key
            // ex: "015efb16-cadf-11ec-9d64-0242ac120002" | "test-person-1"
            redirectService.prepareAndSendEvent("bcm.test.queuing.person.in.redirect", personInput.getId(), personInput);

            return new KeyValue<>(null, null); //discard event which will be caught in the filter step
        }

        PersonOutput personOutput = PersonOutput.builder()
                                                .eventId(personInput.getEventId())
                                                .id(personInput.getId())
                                                .name(personInput.getName())
                                                .job(personInput.getJob())
                                                .deviceId(personInput.getDeviceId())
                                                .build();
        log.info("Output: " + personOutput);
        return new KeyValue<>(personOutput.getId(), personOutput);
    }

    //@Override
    //public KeyValue<String, PersonOutput> recover(EntityMissingInStateStoreRetryableException e, String key, PersonInput personInput) {
    //    int countFailedCallTimes = RetrySynchronizationManager.getContext().getRetryCount();
    //    log.error("--- Could not process {} after {} retries ---", personInput, countFailedCallTimes);
    //    log.warn("--- The application will shutdown in order to re-balancing ---");
    //    System.exit(1);
    //    return null;
    //}

}

