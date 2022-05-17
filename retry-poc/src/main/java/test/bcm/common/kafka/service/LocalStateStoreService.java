package test.bcm.common.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import test.bcm.common.errorhandling.retry.RetryOnLocalStoreImpl;
import test.bcm.common.errorhandling.retry.RetryOnOtherInstanceStoreImpl;
import test.bcm.common.kafka.client.StateStoreClient;
import test.bcm.common.kafka.template.FetchRequestTemplate;

@Slf4j
public abstract class LocalStateStoreService<T> extends StateStoreService {

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
        String storeName = getStateStoreName(type);
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
            log.info("--- StateStore {} obtained successfully ! ---", storeName);
            entity = store.get(id);
        } else {
            log.info("--- Searching in other instance: {}:{} | state store: {} with id: {} and type {}---", hostInfo.host(), hostInfo.port(),
                     storeName, id, type);
            entity = getTmfEventFromOtherInstance.doWithRetry(client, request, hostInfo.host(), hostInfo.port());
            log.info("--- StateStore {} obtained successfully ! ---", storeName);
        }
        return entity;
    }
}
