package test.bcm.example.kafka.service;

import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;
import test.bcm.common.kafka.service.RestStateStoreService;
import test.bcm.common.model.output.DeviceOutput;
import test.bcm.example.kafka.client.DeviceStateStoreClient;

@Service
public class DeviceRestStateStoreService extends RestStateStoreService<DeviceOutput> {

    public DeviceRestStateStoreService(InteractiveQueryService service, DeviceStateStoreClient client) {
        super(service, client);
    }
}
