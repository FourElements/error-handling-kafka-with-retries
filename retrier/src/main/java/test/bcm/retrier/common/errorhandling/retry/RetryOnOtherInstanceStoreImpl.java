package test.bcm.retrier.common.errorhandling.retry;

import test.bcm.retrier.common.constant.GlobalConstant;
import test.bcm.retrier.common.kafka.client.StateStoreClient;
import test.bcm.retrier.common.kafka.template.FetchRequestTemplate;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@NoArgsConstructor
@Service
public abstract class RetryOnOtherInstanceStoreImpl<T> implements RetryOnOtherInstanceStore<T> {

    private static final String STATE_STORE_SHUTDOWN = GlobalConstant.STATE_STORE_SHUTDOWN;
    private int countFailedCallTimes;

    @Override
    public T doWithRetry(StateStoreClient<T> client, FetchRequestTemplate request, String host, int port) {
        //countFailedCallTimes = RetrySynchronizationManager.getContext().getRetryCount();
        //log.warn("--- {} Attempt to get TmfEvent: {} in other instance {}:{} ---", countFailedCallTimes, request.getId(), host, port);
        log.warn("--- Attempt to get TmfEvent: {} in other instance {}:{} ---", request.getId(), host, port);
        return client.invoke(request.getId(), request.getType(), host, port);
    }

    //@Override
    //public T recover(InvalidStateStoreException ex, StateStoreClient<T> client, FetchRequestTemplate request, String host, int port) {
    //    log.error("--- Could not get TmfEvent {} in other instance {}:{} ---", request.getId(), host, port);
    //    log.warn(STATE_STORE_SHUTDOWN);
    //    System.exit(1);
    //    return null;
    //}
    //
    //@Override
    //public T recover(ServerResponseException ex, StateStoreClient<T> client, FetchRequestTemplate request, String host, int port) {
    //    log.error("--- Could not get TmfEvent {} in other instance {}:{} due to a server response error: {} ---", request.getId(), host, port,
    //              ex.getMessage());
    //    log.warn(STATE_STORE_SHUTDOWN);
    //    System.exit(1);
    //    return null;
    //}
    //
    //@Override
    //public T recover(UnexpectedResponseException ex, StateStoreClient<T> client, FetchRequestTemplate request, String host, int port) {
    //    log.error("--- Could not get TmfEvent {} in other instance {}:{} due to an unexpected response: {} ---", request.getId(), host, port,
    //              ex.getMessage());
    //    log.warn(STATE_STORE_SHUTDOWN);
    //    System.exit(1);
    //    return null;
    //}
    //
    //@Override
    //public T recover(Exception ex, StateStoreClient<T> client, FetchRequestTemplate request, String host, int port) {
    //    log.error("--- Could not get TmfEvent {} in other instance {}:{} due to an Unexpected Exception: {} ---", request.getId(), host, port,
    //              ex.getMessage());
    //    log.warn(STATE_STORE_SHUTDOWN);
    //    System.exit(1);
    //    return null;
    //}
    //
    //@Override
    //public int getFailedCallTimes() {
    //    return countFailedCallTimes;
    //}
}
