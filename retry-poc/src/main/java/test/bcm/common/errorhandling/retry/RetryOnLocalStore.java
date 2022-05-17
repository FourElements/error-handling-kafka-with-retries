package test.bcm.common.errorhandling.retry;

import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import test.bcm.common.kafka.template.FetchRequestTemplate;

@Service
public interface RetryOnLocalStore<T> {

    //@Retryable(include = {InvalidStateStoreException.class},
    //    backoff = @Backoff(delayExpression = "${retry.kafka.state-store-connection.delay}", multiplierExpression = "${retry.kafka.state-store-connection.multiplier}",
    //        maxDelayExpression = "${retry.kafka.state-store-connection.max-delay}"), maxAttemptsExpression = "${retry.kafka.state-store-connection.max-retry}")
    ReadOnlyKeyValueStore<String, T> doWithRetry(InteractiveQueryService service, FetchRequestTemplate request);

    //@Recover
    //ReadOnlyKeyValueStore<String, T> recover(InvalidStateStoreException ex, InteractiveQueryService service,
    //    FetchRequestTemplate request);
    //
    //int getFailedCallTimes();
}
