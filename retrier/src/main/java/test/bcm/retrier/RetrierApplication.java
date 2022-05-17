package test.bcm.retrier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.schema.client.EnableSchemaRegistryClient;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableSchemaRegistryClient
@EnableRetry
public class RetrierApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetrierApplication.class, args);
    }

}
