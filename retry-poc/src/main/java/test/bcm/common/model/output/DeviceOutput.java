package test.bcm.common.model.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import test.bcm.common.model.BaseEvent;

import java.io.Serializable;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = DeviceOutput.DeviceOutputBuilderImpl.class)
public class DeviceOutput extends BaseEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("description")
    private String description;

    @JsonProperty("changesCount")
    private Integer changesCount;

    @JsonPOJOBuilder(withPrefix = "")
    static final class DeviceOutputBuilderImpl extends DeviceOutput.DeviceOutputBuilder<DeviceOutput, DeviceOutputBuilderImpl> {

    }

}
