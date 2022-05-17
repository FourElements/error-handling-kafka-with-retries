package test.bcm.retrier.example.kafka.retry;

import test.bcm.retrier.common.errorhandling.retry.RetryOnOtherInstanceStoreImpl;
import test.bcm.retrier.common.model.output.DeviceOutput;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@NoArgsConstructor
@Service
public class DeviceRetryOnOtherInstanceStoreImpl extends RetryOnOtherInstanceStoreImpl<DeviceOutput> {
}
