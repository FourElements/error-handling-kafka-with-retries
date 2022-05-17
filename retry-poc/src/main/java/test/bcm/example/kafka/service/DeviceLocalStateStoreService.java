package test.bcm.example.kafka.service;

import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;
import test.bcm.common.kafka.service.LocalStateStoreService;
import test.bcm.common.model.output.DeviceOutput;
import test.bcm.example.kafka.client.DeviceStateStoreClient;
import test.bcm.example.kafka.retry.DeviceRetryOnLocalStoreImpl;
import test.bcm.example.kafka.retry.DeviceRetryOnOtherInstanceStoreImpl;

@Service
public class DeviceLocalStateStoreService extends LocalStateStoreService<DeviceOutput> {

    public DeviceLocalStateStoreService(InteractiveQueryService service, DeviceStateStoreClient client,
        DeviceRetryOnLocalStoreImpl retryOnGetLocalKeyValueStore, DeviceRetryOnOtherInstanceStoreImpl retryOnGetTmfEventFromOtherInstance) {
        super(service, client, retryOnGetLocalKeyValueStore, retryOnGetTmfEventFromOtherInstance);
    }
}