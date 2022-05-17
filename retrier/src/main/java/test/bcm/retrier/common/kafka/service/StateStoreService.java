package test.bcm.retrier.common.kafka.service;

import test.bcm.retrier.example.constant.ExampleConstant;

public abstract class StateStoreService {

    String getStateStoreName(String type) {
        String storeName;
        if (ExampleConstant.TypeEnum.DEVICE.getValue().equalsIgnoreCase(type)) {
            storeName = ExampleConstant.DEVICE_KTABLE_EVENT_STORE;
        } else {
            throw new IllegalArgumentException("--- Unexpected type: " + type + "received. No state store exists for that specific type. ---");
        }

        return storeName;
    }

}
