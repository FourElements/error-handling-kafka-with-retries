package test.bcm.retrier.common.errorhandling.exceptions;

public class ServerResponseException extends RuntimeException {

    public ServerResponseException() {
        super();
    }

    public ServerResponseException(String s) {
        super(s);
    }
}
