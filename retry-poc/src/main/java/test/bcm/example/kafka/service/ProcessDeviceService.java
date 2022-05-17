package test.bcm.example.kafka.service;

import org.apache.kafka.streams.KeyValue;
import org.springframework.stereotype.Service;
import test.bcm.common.model.input.DeviceInput;
import test.bcm.common.model.output.DeviceOutput;

@Service
public interface ProcessDeviceService {

    KeyValue<String, DeviceOutput> inputToOutput(String key, DeviceInput deviceInput);

}
