package test.bcm.example.kafka.retry;

import org.springframework.stereotype.Service;
import test.bcm.common.errorhandling.retry.RetryOnLocalStoreImpl;
import test.bcm.common.model.output.DeviceOutput;

@Service
public class DeviceRetryOnLocalStoreImpl extends RetryOnLocalStoreImpl<DeviceOutput> {
}
