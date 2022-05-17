package test.bcm.common.errorhandling.template;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class ErrorTemplate {

    @NotNull
    private final String errorCode;

    @NotNull
    private final String errorMessage;
    private final String errorDescription;
}
