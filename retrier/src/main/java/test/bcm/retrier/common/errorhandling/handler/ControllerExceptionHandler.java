package test.bcm.retrier.common.errorhandling.handler;

import test.bcm.retrier.common.errorhandling.template.ErrorTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(value = {InvalidStateStoreException.class})
    public ResponseEntity<ErrorTemplate> resourceNotFoundException(Exception ex) {
        return createErrorTemplateResponseEntity("500", "StateStore Unavailable", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    protected ResponseEntity<ErrorTemplate> createErrorTemplateResponseEntity(String errorCode, String errorDescription, String errorMessage,
        HttpStatus httpStatus) {

        ErrorTemplate build = ErrorTemplate.builder()
                                           .errorCode(errorCode)
                                           .errorDescription(errorDescription)
                                           .errorMessage(errorMessage)
                                           .build();

        return ResponseEntity.status(httpStatus).body(build);
    }
}
