package test.bcm.retrier.example.kafka.retry;

import test.bcm.retrier.common.errorhandling.retry.RetryOnLocalStoreImpl;
import test.bcm.retrier.common.model.output.DeviceOutput;
import org.springframework.stereotype.Service;

@Service
public class DeviceRetryOnLocalStoreImpl extends RetryOnLocalStoreImpl<DeviceOutput> {
}
