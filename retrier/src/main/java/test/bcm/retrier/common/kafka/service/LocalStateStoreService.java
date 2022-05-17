package test.bcm.retrier.common.kafka.service;

import test.bcm.retrier.common.kafka.client.StateStoreClient;
import test.bcm.retrier.common.kafka.template.FetchRequestTemplate;
import test.bcm.retrier.common.errorhandling.retry.RetryOnLocalStoreImpl;
import test.bcm.retrier.common.errorhandling.retry.RetryOnOtherInstanceStoreImpl;
import test.bcm.retrier.example.constant.ExampleConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;

@Slf4j
public abstract class LocalStateStoreService<T> extends StateStoreService {

    public static final String STATE_STORE_OBTAINED_SUCCESSFULLY = "--- StateStore {} obtained successfully ! ---";

    private final InteractiveQueryService service;
    private final StateStoreClient<T> client;
    private final RetryOnLocalStoreImpl<T> getLocalKeyValueStore;
    private final RetryOnOtherInstanceStoreImpl<T> getTmfEventFromOtherInstance;

    protected LocalStateStoreService(InteractiveQueryService service, StateStoreClient<T> client, RetryOnLocalStoreImpl<T> getLocalKeyValueStore,
        RetryOnOtherInstanceStoreImpl<T> getTmfEventFromOtherInstance) {
        this.service = service;
        this.client = client;
        this.getLocalKeyValueStore = getLocalKeyValueStore;
        this.getTmfEventFromOtherInstance = getTmfEventFromOtherInstance;
    }

    public T fetchByIdAndType(String id, String type) {
        //String storeName = getStateStoreName(type);
        String storeName = ExampleConstant.DEVICE_OUT_EVENT_STORE;
        HostInfo hostInfo = service.getHostInfo(storeName, id, Serdes.String().serializer());
        FetchRequestTemplate request = FetchRequestTemplate.builder()
                                                           .id(id)
                                                           .type(type)
                                                           .storeName(storeName)
                                                           .build();
        T entity;
        log.warn("LOCAL BCM HOST INFO: {}", hostInfo.toString());
        if (service.getCurrentHostInfo().equals(hostInfo)) {
            log.info("--- Searching in localhost: {}:{} | state store: {} with id: {} and type {}---", hostInfo.host(), hostInfo.port(), storeName,
                     id, type);
            ReadOnlyKeyValueStore<String, T> store = getLocalKeyValueStore.doWithRetry(service, request);
            log.info(STATE_STORE_OBTAINED_SUCCESSFULLY, storeName);
            entity = store.get(id);
        } else {
            log.info("--- Searching in other instance: {}:{} | state store: {} with id: {} and type {}---", hostInfo.host(), hostInfo.port(),
                     storeName, id, type);
            entity = getTmfEventFromOtherInstance.doWithRetry(client, request, hostInfo.host(), hostInfo.port());
            log.info(STATE_STORE_OBTAINED_SUCCESSFULLY, storeName);
        }
        return entity;
    }

    //public T fetchByIdAndStoreName(String id, String storeName) {
    //    HostInfo hostInfo = service.getHostInfo(storeName, id, Serdes.String().serializer());
    //    FetchRequestTemplate request = FetchRequestTemplate.builder()
    //                                                       .id(id)
    //                                                       .type(null)
    //                                                       .storeName(storeName)
    //                                                       .build();
    //    T entity;
    //    log.warn("LOCAL BCM HOST INFO: {}", hostInfo.toString());
    //    if (service.getCurrentHostInfo().equals(hostInfo)) {
    //        log.info("--- Searching in localhost: {}:{} | state store: {} with id: {} ---", hostInfo.host(), hostInfo.port(), storeName, id);
    //        ReadOnlyKeyValueStore<String, T> store = getLocalKeyValueStore.doWithRetry(service, request);
    //        log.info(STATE_STORE_OBTAINED_SUCCESSFULLY, storeName);
    //        entity = store.get(id);
    //    } else {
    //        log.info("--- Searching in other instance: {}:{} | state store: {} with id: {} ---", hostInfo.host(), hostInfo.port(), storeName, id);
    //        entity = getTmfEventFromOtherInstance.doWithRetry(client, request, hostInfo.host(), hostInfo.port());
    //        log.info(STATE_STORE_OBTAINED_SUCCESSFULLY, storeName);
    //    }
    //    return entity;
    //}
}
