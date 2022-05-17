package test.bcm.retrier.common.errorhandling.retry;

import test.bcm.retrier.common.constant.GlobalConstant;
import test.bcm.retrier.common.kafka.template.FetchRequestTemplate;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;

@Slf4j
@NoArgsConstructor
@Service
public abstract class RetryOnLocalStoreImpl<T> implements RetryOnLocalStore<T> {

    private static final String STATE_STORE_SHUTDOWN = GlobalConstant.STATE_STORE_SHUTDOWN;
    private int countFailedCallTimes;

    @Override
    public ReadOnlyKeyValueStore<String, T> doWithRetry(InteractiveQueryService service, FetchRequestTemplate request) {
        //countFailedCallTimes = RetrySynchronizationManager.getContext().getRetryCount();
        //log.warn("--- {} Attempt to get local state store {} ---", countFailedCallTimes, request.getStoreName());
        log.warn("--- Attempt to get local state store {} ---", request.getStoreName());
        return service.getQueryableStore(request.getStoreName(), QueryableStoreTypes.keyValueStore());
    }

    //@Override
    //public ReadOnlyKeyValueStore<String, T> recover(InvalidStateStoreException ex, InteractiveQueryService service,
    //    FetchRequestTemplate request) {
    //    log.error("--- Could not get StateStore {} after retry ---", request.getStoreName());
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
