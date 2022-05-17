package test.bcm.example.kafka.reducer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.Reducer;
import org.springframework.stereotype.Component;
import test.bcm.common.model.output.DeviceOutput;

@Slf4j
@Component
public class DeviceReducer implements Reducer<DeviceOutput> {

    @Override
    public DeviceOutput apply(DeviceOutput value1, DeviceOutput value2) {
        if (value2 == null) {
            return null;
        }
        Integer previousChangesCount;
        if (value1 == null || value1.getChangesCount() == null) {
            previousChangesCount = 0;
        } else {
            previousChangesCount = value1.getChangesCount();
        }
        value2.setChangesCount(previousChangesCount+1);
        log.info("Reducer result: " + value2);
        return value2;
    }
}
