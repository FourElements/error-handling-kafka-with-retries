package test.bcm.retrier.example.kafka.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import test.bcm.retrier.common.model.input.DeviceInput;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class CustomDeviceInputJsonDeserializer implements Deserializer<DeviceInput> {

    private final ObjectMapper objectMapper;

    public CustomDeviceInputJsonDeserializer() {
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).findAndRegisterModules();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // NO-OP
    }

    @Override
    public DeviceInput deserialize(String s, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.readValue(data, new TypeReference<>() {});
        } catch (Exception e) {
            throw new SerializationException("Reduce can't deserialize data from store", e);
        }
    }

    @Override
    public DeviceInput deserialize(String topic, Headers headers, byte[] data) {
        return deserialize(topic, data);
    }

    @Override
    public void close() {
        // NO-OP
    }
}
