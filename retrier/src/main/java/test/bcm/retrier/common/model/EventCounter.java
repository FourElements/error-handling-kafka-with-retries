package test.bcm.retrier.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = EventCounter.EventCounterBuilderImpl.class)
public class EventCounter extends BaseEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("count")
    private Integer count;


    @JsonPOJOBuilder(withPrefix = "")
    static final class EventCounterBuilderImpl extends EventCounter.EventCounterBuilder<EventCounter, EventCounter.EventCounterBuilderImpl> {

    }
}
