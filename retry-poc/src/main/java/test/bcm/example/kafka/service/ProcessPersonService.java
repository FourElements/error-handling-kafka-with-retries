package test.bcm.example.kafka.service;

import org.apache.kafka.streams.KeyValue;
import test.bcm.common.errorhandling.exceptions.EntityMissingInStateStoreRetryableException;
import test.bcm.common.model.input.PersonInput;
import test.bcm.common.model.output.PersonOutput;

public interface ProcessPersonService {

    //@Retryable(include = {EntityMissingInStateStoreRetryableException.class},
    //    backoff = @Backoff(delayExpression = "${retry.kafka.process-device.delay}", multiplierExpression = "${retry.kafka.process-device.multiplier}",
    //        maxDelayExpression = "${retry.kafka.process-device.max-delay}"), maxAttemptsExpression = "${retry.kafka.process-device.max-retry}")
    KeyValue<String, PersonOutput> inputToOutput(String key, PersonInput personInput);

    //@Recover
    //KeyValue<String, PersonOutput> recover(EntityMissingInStateStoreRetryableException e, String key, PersonInput personInput);

}
