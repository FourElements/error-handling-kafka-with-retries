package test.bcm.example.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import test.bcm.common.model.input.PersonInput;
import test.bcm.common.model.output.DeviceOutput;
import test.bcm.example.constant.ExampleConstant;
import test.bcm.example.kafka.service.DeviceLocalStateStoreService;

@Slf4j
@Service
public class PersonValidator {

    private final DeviceLocalStateStoreService stateStoreService;

    public PersonValidator(DeviceLocalStateStoreService stateStoreService) {
        this.stateStoreService = stateStoreService;
    }

    public boolean validateIfDeviceExistsInStateStore(PersonInput personInput) {
        if (personInput.getDeviceId() == null) {
            log.warn("--- Could not process PersonInput as deviceId is null ---");
            return false;
        }

        DeviceOutput deviceOutput = stateStoreService.fetchByIdAndType(
            personInput.getDeviceId(),
            ExampleConstant.TypeEnum.DEVICE.getValue()
        );

        // if device is not found in state store then return false
        return deviceOutput != null;
    }

}
