package test.bcm.example.kafka.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Reducer;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Service;
import test.bcm.common.model.input.DeviceInput;
import test.bcm.common.model.output.DeviceOutput;
import test.bcm.example.constant.ExampleConstant;
import test.bcm.example.kafka.reducer.DeviceReducer;
import test.bcm.example.kafka.service.ProcessDeviceService;
import test.bcm.example.kafka.service.ProcessDeviceServiceImpl;
import test.bcm.example.util.CustomDeviceJsonDeserializer;

import java.util.function.Function;

@Service
@Slf4j
public class DeviceProcessor {

    private final ProcessDeviceService processDeviceService;
    private final Reducer<DeviceOutput> reducer;

    public DeviceProcessor(ProcessDeviceServiceImpl processService, DeviceReducer reducer) {
        this.processDeviceService = processService;
        this.reducer = reducer;
    }

    @Bean
    public Function<KStream<String, DeviceInput>, KStream<String, DeviceOutput>> processDeviceEvent() {

        return input -> input
            .selectKey((key, value) -> value.getId())
            .transformValues(() -> new ValueTransformer<DeviceInput, DeviceOutput>() {
                private ProcessorContext context;

                @Override
                public void init(ProcessorContext context) {
                    this.context = context;
                }

                @Override
                public DeviceOutput transform(DeviceInput value) {
                    log.info("--- Attempt to process input {} ---", value);
                    log.info("--- This task has the following timestamp: {}", context.timestamp());

                    DeviceOutput deviceOutput = DeviceOutput.builder()
                                                            .eventId(value.getEventId())
                                                            .id(value.getId())
                                                            .description(value.getDescription())
                                                            .build();

                    log.info("Output: " + deviceOutput);
                    return deviceOutput;
                }

                @Override
                public void close() {
                    //NO-OP
                }
            })
            //.map(processDeviceService::inputToOutput)
            .groupByKey(Grouped.with(Serdes.String(), Serdes.serdeFrom(new JsonSerializer<>(), new CustomDeviceJsonDeserializer())))
            .reduce(reducer, Materialized
                .<String, DeviceOutput, KeyValueStore<Bytes, byte[]>>as(ExampleConstant.DEVICE_KTABLE_EVENT_STORE)
                .withKeySerde(Serdes.String())
                .withValueSerde(Serdes.serdeFrom(new JsonSerializer<>(), new CustomDeviceJsonDeserializer())))
            .toStream()
            .peek((key, value) -> log.info("Successfully processed device with key: {} and value: {}", key, value));
    }

}