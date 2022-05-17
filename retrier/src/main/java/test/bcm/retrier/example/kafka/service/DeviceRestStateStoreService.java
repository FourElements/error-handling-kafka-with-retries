package test.bcm.retrier.example.kafka.service;

import test.bcm.retrier.common.kafka.service.RestStateStoreService;
import test.bcm.retrier.common.model.output.DeviceOutput;
import test.bcm.retrier.example.kafka.client.DeviceStateStoreClient;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;

@Service
public class DeviceRestStateStoreService extends RestStateStoreService<DeviceOutput> {

    public DeviceRestStateStoreService(InteractiveQueryService service, DeviceStateStoreClient client) {
        super(service, client);
    }
}
