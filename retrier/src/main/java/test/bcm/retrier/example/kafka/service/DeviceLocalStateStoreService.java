package test.bcm.retrier.example.kafka.service;

import test.bcm.retrier.common.kafka.service.LocalStateStoreService;
import test.bcm.retrier.common.model.output.DeviceOutput;
import test.bcm.retrier.example.kafka.client.DeviceStateStoreClient;
import test.bcm.retrier.example.kafka.retry.DeviceRetryOnLocalStoreImpl;
import test.bcm.retrier.example.kafka.retry.DeviceRetryOnOtherInstanceStoreImpl;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;

@Service
public class DeviceLocalStateStoreService extends LocalStateStoreService<DeviceOutput> {

    public DeviceLocalStateStoreService(InteractiveQueryService service, DeviceStateStoreClient client,
        DeviceRetryOnLocalStoreImpl retryOnGetLocalKeyValueStore, DeviceRetryOnOtherInstanceStoreImpl retryOnGetTmfEventFromOtherInstance) {
        super(service, client, retryOnGetLocalKeyValueStore, retryOnGetTmfEventFromOtherInstance);
    }
}