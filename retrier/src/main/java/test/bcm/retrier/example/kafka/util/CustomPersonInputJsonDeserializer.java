package test.bcm.retrier.example.kafka.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import test.bcm.retrier.common.model.input.PersonInput;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Arrays;
import java.util.Map;

@Slf4j
public class CustomPersonInputJsonDeserializer implements Deserializer<PersonInput> {

    private final ObjectMapper objectMapper;

    public CustomPersonInputJsonDeserializer() {
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).findAndRegisterModules();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // NO-OP
    }

    @Override
    @SuppressWarnings("unchecked")
    public PersonInput deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.readValue(data, PersonInput.class);
        } catch (Exception e) {
            throw new SerializationException("Can't deserialize data [" + Arrays.toString(data) + "] from topic [" + topic + "]", e);
        }
    }

    @Override
    public PersonInput deserialize(String topic, Headers headers, byte[] data) {
        return deserialize(topic, data);
    }

    @Override
    public void close() {
        // NO-OP
    }
}
