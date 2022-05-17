package test.bcm.common.model.input;

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
@JsonDeserialize(builder = DeviceInput.DeviceInputBuilderImpl.class)
public class DeviceInput extends BaseEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("description")
    private String description;


    @JsonPOJOBuilder(withPrefix = "")
    static final class DeviceInputBuilderImpl extends DeviceInput.DeviceInputBuilder<DeviceInput, DeviceInputBuilderImpl> {

    }

}
