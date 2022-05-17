package test.bcm.retrier.common.errorhandling.exceptions;

public class UnexpectedResponseException extends RuntimeException {

    public UnexpectedResponseException() {
        super();
    }

    public UnexpectedResponseException(String s) {
        super(s);
    }
}
