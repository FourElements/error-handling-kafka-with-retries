package test.bcm.common.kafka.client;

import io.opentracing.contrib.spring.web.client.TracingExchangeFilterFunction;
import io.opentracing.contrib.spring.web.client.WebClientSpanDecorator;
import io.opentracing.util.GlobalTracer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import test.bcm.common.errorhandling.exceptions.ServerResponseException;
import test.bcm.common.errorhandling.exceptions.UnexpectedResponseException;

import java.util.Collections;

@Slf4j
@Getter
public abstract class StateStoreClient<T> {

    private final WebClient client;

    private static final String STATE_STORE_UNAVAILABLE = "500";

    protected StateStoreClient() {
        this.client = WebClient.builder()
                               .filter(new TracingExchangeFilterFunction(GlobalTracer.get(),
                                                                         Collections.singletonList(new WebClientSpanDecorator.StandardTags())))
                               .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                               .build();
    }

    public abstract T invoke(String id, String type, String host, int port);

    public void responseHandler(ClientResponse response) {
        if (response == null) {
            log.error("--- Unexpected null response ---");
            throw new UnexpectedResponseException("Unexpected null response");
        }
        if (response.statusCode().is4xxClientError()) {
            log.error("--- Unexpected response status | error code: {} ---", response.statusCode());
            throw new UnexpectedResponseException("Unexpected response status");
        } else if (response.statusCode().is5xxServerError()) {
            log.error("--- Error when retrieving state store | error code: {} ---", response.statusCode());
            throw new ServerResponseException("Error when retrieving state store");
        } else {
            log.debug("--- Response with success ---");
        }
    }
}
