package test.bcm.example.kafka.retry;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import test.bcm.common.errorhandling.retry.RetryOnOtherInstanceStoreImpl;
import test.bcm.common.model.output.DeviceOutput;

@NoArgsConstructor
@Service
public class DeviceRetryOnOtherInstanceStoreImpl extends RetryOnOtherInstanceStoreImpl<DeviceOutput> {
}
