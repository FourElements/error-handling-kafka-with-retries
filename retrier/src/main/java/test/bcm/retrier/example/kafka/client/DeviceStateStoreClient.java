package test.bcm.retrier.example.kafka.client;

import test.bcm.retrier.common.errorhandling.exceptions.ServerResponseException;
import test.bcm.retrier.common.kafka.client.StateStoreClient;
import test.bcm.retrier.common.model.output.DeviceOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;

import java.net.ConnectException;
import java.util.Objects;

@Slf4j
@Component
public class DeviceStateStoreClient extends StateStoreClient<DeviceOutput> {

    public DeviceStateStoreClient() {
        super();
    }

    @Override
    public DeviceOutput invoke(String id, String type, String host, int port) {
        log.info("BCM HOST: {}", host);
        log.info("BCM PORT: {}", port);
        ClientResponse response = super.getClient().get()
                                       .uri(uriBuilder -> uriBuilder.scheme("http")
                                                                    .host(host)
                                                                    .port(port)
                                                                    .path("/stateStore-api/example/v1/device/{id}")
                                                                    .queryParam("type", type)
                                                                    .build(id))
                                       .exchange()
                                       .onErrorMap(ConnectException.class, t -> new ServerResponseException(t.getMessage()))
                                       .block();
        // perform validations on received response
        responseHandler(response);

        return Objects.requireNonNull(response).bodyToMono(new ParameterizedTypeReference<DeviceOutput>() {
        }).block();
    }
}
