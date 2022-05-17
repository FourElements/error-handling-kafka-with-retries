package test.bcm.retrier.common.kafka.template;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FetchRequestTemplate {

    private final String id;
    private final String type;
    private final String storeName;
}
