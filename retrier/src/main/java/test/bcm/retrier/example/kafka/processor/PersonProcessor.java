package test.bcm.retrier.example.kafka.processor;

import test.bcm.retrier.example.kafka.service.ProducerService;
import test.bcm.retrier.example.kafka.service.RedirectProcess;
import test.bcm.retrier.example.kafka.service.RetryProducerService;
import test.bcm.retrier.example.kafka.service.TombstoneObjectProducerService;
import test.bcm.retrier.example.kafka.service.TransactionalProducerService;
import test.bcm.retrier.example.kafka.util.PersonValidator;
import test.bcm.retrier.common.model.EventCounter;
import test.bcm.retrier.common.model.input.PersonInput;
import test.bcm.retrier.common.model.output.DeviceOutput;
import test.bcm.retrier.common.model.output.PersonOutput;
import test.bcm.retrier.common.serialization.CustomSerdes;
import test.bcm.retrier.example.constant.ExampleConstant;
import test.bcm.retrier.example.kafka.service.DlqProducerService;
import test.bcm.retrier.example.kafka.service.TombstoneProducerService;
import test.bcm.retrier.example.kafka.transformer.PersonProcessorTransformer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
public class PersonProcessor {

    private final PersonValidator validator;
    private final ProducerService retryService;
    private final ProducerService tombstoneService;
    private final TransactionalProducerService transactionalProducerService;
    private final ProducerService dlqService;
    private final InteractiveQueryService service;
    private final ProducerService tombstoneObjectService;

    public PersonProcessor(PersonValidator validator, RetryProducerService retryService, TombstoneProducerService tombstoneService,
        TransactionalProducerService transactionalProducerService, DlqProducerService dlqService, InteractiveQueryService service,
        TombstoneObjectProducerService tombstoneObjectService) {
        this.validator = validator;
        this.retryService = retryService;
        this.tombstoneService = tombstoneService;
        this.transactionalProducerService = transactionalProducerService;
        this.dlqService = dlqService;
        this.service = service;
        this.tombstoneObjectService = tombstoneObjectService;
    }

    @Bean
    @SuppressWarnings("unchecked")
    public StoreBuilder<KeyValueStore<String, List<String>>> inMemoryKeyValueStoreRedirectPerson() {
        return Stores.keyValueStoreBuilder(
            Stores.inMemoryKeyValueStore(ExampleConstant.REDIRECT_PERSON_IN_MEMORY_EVENT_STORE),
            Serdes.String(),
            CustomSerdes.ListSerde(ArrayList.class, Serdes.String()));
    }

    @Bean
    public Function<KStream<String, PersonInput>, Function<KTable<String, DeviceOutput>, Function<GlobalKTable<String, EventCounter>,
        Function<KStream<String, String>, KStream<String, PersonOutput>>>>> retryProcessPersonEvent() {

        return input -> (
            deviceOutputTable -> (
                eventCounterGlobalTable -> (
                    redirectInfo -> {
                        redirectInfo.process(
                            () -> new RedirectProcess(tombstoneObjectService), ExampleConstant.REDIRECT_PERSON_IN_MEMORY_EVENT_STORE);
                        return (
                            input.transform(
                                () -> new PersonProcessorTransformer(validator, retryService, tombstoneService, transactionalProducerService,
                                                                     dlqService, service), ExampleConstant.DEVICE_OUT_EVENT_STORE)
                                 .peek((key, value) -> log.info("Successfully processed person with key: {} and value: {}", key, value))
                        );
                    }
                )
            )
        );
    }
}