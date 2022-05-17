package test.bcm.common.errorhandling.retry;

import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import test.bcm.common.errorhandling.exceptions.ServerResponseException;
import test.bcm.common.errorhandling.exceptions.UnexpectedResponseException;
import test.bcm.common.kafka.client.StateStoreClient;
import test.bcm.common.kafka.template.FetchRequestTemplate;

@Service
public interface RetryOnOtherInstanceStore<T> {

    //@Retryable(include = {InvalidStateStoreException.class, ServerResponseException.class, UnexpectedResponseException.class, Exception.class},
    //    backoff = @Backoff(delayExpression = "${retry.kafka.state-store-connection.delay}", multiplierExpression = "${retry.kafka.state-store-connection.multiplier}",
    //        maxDelayExpression = "${retry.kafka.state-store-connection.max-delay}"), maxAttemptsExpression = "${retry.kafka.state-store-connection.max-retry}")
    T doWithRetry(StateStoreClient<T> client, FetchRequestTemplate request, String host, int port);

    //@Recover
    //T recover(InvalidStateStoreException ex, StateStoreClient<T> client, FetchRequestTemplate request, String host, int port);
    //
    //@Recover
    //T recover(ServerResponseException ex, StateStoreClient<T> client, FetchRequestTemplate request, String host, int port);
    //
    //@Recover
    //T recover(UnexpectedResponseException ex, StateStoreClient<T> client, FetchRequestTemplate request, String host, int port);
    //
    //@Recover
    //T recover(Exception ex, StateStoreClient<T> client, FetchRequestTemplate request, String host, int port);
    //
    //int getFailedCallTimes();
}
