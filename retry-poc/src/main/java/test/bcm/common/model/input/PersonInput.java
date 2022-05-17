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
@JsonDeserialize(builder = PersonInput.PersonInputBuilderImpl.class)
public class PersonInput extends BaseEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("name")
    private String name;

    @JsonProperty("job")
    private String job;

    @JsonProperty("deviceId")
    private String deviceId;


    @JsonPOJOBuilder(withPrefix = "")
    static final class PersonInputBuilderImpl extends PersonInputBuilder<PersonInput, PersonInputBuilderImpl> {

    }

}
