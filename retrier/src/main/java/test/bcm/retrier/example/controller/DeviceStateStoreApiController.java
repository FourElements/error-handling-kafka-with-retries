package test.bcm.retrier.example.controller;

import test.bcm.retrier.common.kafka.service.RestStateStoreService;
import test.bcm.retrier.example.kafka.service.DeviceRestStateStoreService;
import test.bcm.retrier.common.model.output.DeviceOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value = "/stateStore-api/example/v1/")
public class DeviceStateStoreApiController implements DeviceStateStoreApi {

    private final RestStateStoreService<DeviceOutput> service;

    public DeviceStateStoreApiController(DeviceRestStateStoreService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<DeviceOutput> retrieveDevice(String id, @Valid String type, @Valid String fields) {
        DeviceOutput deviceOutput = service.fetchByIdAndType(id, type);
        return new ResponseEntity<>(deviceOutput, HttpStatus.OK);
    }
}
