package test.bcm.common.errorhandling.retry;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;
import test.bcm.common.constant.GlobalConstant;
import test.bcm.common.kafka.template.FetchRequestTemplate;

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
