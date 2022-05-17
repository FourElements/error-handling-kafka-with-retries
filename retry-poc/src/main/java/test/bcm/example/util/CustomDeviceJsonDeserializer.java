package test.bcm.example.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import test.bcm.common.model.output.DeviceOutput;

import java.util.Map;

public class CustomDeviceJsonDeserializer implements Deserializer<DeviceOutput> {

    private final ObjectMapper objectMapper;

    public CustomDeviceJsonDeserializer() {
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).findAndRegisterModules();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // NO-OP
    }

    @Override
    public DeviceOutput deserialize(String s, byte[] data) {
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
    public DeviceOutput deserialize(String topic, Headers headers, byte[] data) {
        return deserialize(topic, data);
    }

    @Override
    public void close() {
        // NO-OP
    }
}
