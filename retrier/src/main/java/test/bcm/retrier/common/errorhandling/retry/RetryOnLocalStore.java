package test.bcm.retrier.common.errorhandling.retry;

import test.bcm.retrier.common.kafka.template.FetchRequestTemplate;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;

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
