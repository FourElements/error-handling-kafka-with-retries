package test.bcm.example.kafka.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import test.bcm.common.model.input.PersonInput;
import test.bcm.common.model.output.PersonOutput;
import test.bcm.common.serialization.CustomSerdes;
import test.bcm.example.constant.ExampleConstant;
import test.bcm.example.kafka.retry.RetryTransformer;
import test.bcm.example.kafka.service.IRetryRedirectFacade;
import test.bcm.example.kafka.service.PersonProcessorTransformer;
import test.bcm.example.kafka.service.RedirectProcess;
import test.bcm.example.kafka.service.RetryRedirectFacade;
import test.bcm.example.util.PersonValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

@Service
@Slf4j
public class PersonProcessor {

    private final PersonValidator validator;
    private final IRetryRedirectFacade<String> facade;

    public PersonProcessor(PersonValidator validator, RetryRedirectFacade facade) {
        this.validator = validator;
        this.facade = facade;
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
    public BiFunction<KStream<String, PersonInput>, KStream<String, String>, KStream<String, PersonOutput>> processPersonEvent() {

        return (input, redirectInfo) -> {
            // load redirect-topic and create/update redirect-person in memory store (which is an aggregated view)
            redirectInfo.process(RedirectProcess::new, ExampleConstant.REDIRECT_PERSON_IN_MEMORY_EVENT_STORE);
            // main flow to process events
            return input
                // verify if it can actually be processed or if is already being redirected
                .transform(() -> new RetryTransformer<>(facade), ExampleConstant.REDIRECT_PERSON_IN_MEMORY_EVENT_STORE)
                // attempt to process it - or redirects or successfully processes
                .transform(() -> new PersonProcessorTransformer(validator, facade), ExampleConstant.REDIRECT_PERSON_IN_MEMORY_EVENT_STORE)
                .peek((key, value) -> log.info("Successfully processed person with key: {} and value: {}", key, value));
        };
    }
}