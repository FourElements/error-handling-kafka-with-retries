package test.bcm.retrier.example.kafka.util;

import test.bcm.retrier.common.model.BaseEvent;
import test.bcm.retrier.common.model.EventCounter;
import test.bcm.retrier.common.model.input.DeviceInput;
import test.bcm.retrier.common.model.input.PersonInput;
import test.bcm.retrier.common.model.output.DeviceOutput;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class AppSerdes extends Serdes {

    public static final class CustomPersonSerde extends WrapperSerde<PersonInput> {

        public CustomPersonSerde() {
            super(new JsonSerializer<>(), new CustomPersonInputJsonDeserializer());
        }
    }


    public static final class CustomSerde extends WrapperSerde<BaseEvent> {

        public CustomSerde() {
            super(new JsonSerializer<>(), new CustomJsonDeserializer());
        }
    }


    public static final class CustomDeviceInSerde extends WrapperSerde<DeviceInput> {

        public CustomDeviceInSerde() {
            super(new JsonSerializer<>(), new CustomDeviceInputJsonDeserializer());
        }
    }


    public static final class CustomDeviceOutSerde extends WrapperSerde<DeviceOutput> {

        public CustomDeviceOutSerde() {
            super(new JsonSerializer<>(), new CustomDeviceOutputJsonDeserializer());
        }
    }


    public static final class CustomEventCounterSerde extends WrapperSerde<EventCounter> {

        public CustomEventCounterSerde() {
            super(new JsonSerializer<>(), new CustomEventCounterDeserializer());
        }
    }

    public static CustomDeviceInSerde DeviceInputSerde() {
        return new CustomDeviceInSerde();
    }
}
