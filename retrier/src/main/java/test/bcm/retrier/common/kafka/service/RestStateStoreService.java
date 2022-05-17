package test.bcm.retrier.common.kafka.service;

import test.bcm.retrier.common.kafka.client.StateStoreClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;

@Slf4j
public abstract class RestStateStoreService<T> extends StateStoreService {

    private final InteractiveQueryService service;
    private final StateStoreClient<T> client;

    protected RestStateStoreService(InteractiveQueryService service, StateStoreClient<T> client) {
        this.service = service;
        this.client = client;
    }

    public T fetchByIdAndType(String id, String type) {
        String storeName = getStateStoreName(type);

        log.warn("REST BCM storeName: {}, id: {}, type: {}", storeName, id, type);

        HostInfo hostInfo = service.getHostInfo(storeName, id, Serdes.String().serializer());
        T entity;
        log.warn("REST BCM HOST INFO: {}", hostInfo.toString());
        if (service.getCurrentHostInfo().equals(hostInfo)) {
            log.info("--- Rest Service | Searching in localhost: {}:{} | state store: {} with id: {} and type {}---", hostInfo.host(),
                     hostInfo.port(), storeName, id, type);
            ReadOnlyKeyValueStore<String, T> store = service.getQueryableStore(storeName, QueryableStoreTypes.keyValueStore());
            log.info("--- StateStore {} obtained successfully ! ---", storeName);
            entity = store.get(id);
            log.info("--- StateStore get by id {} result: {}  ! ---", id, entity);
        } else {
            log.info("--- Searching in other instance: {}:{} | state store: {} with id: {} and type {}---", hostInfo.host(), hostInfo.port(),
                     storeName, id, type);
            entity = client.invoke(id, type, hostInfo.host(), hostInfo.port());
            log.info("--- StateStore {} obtained successfully ! ---", storeName);
        }
        return entity;
    }
}
