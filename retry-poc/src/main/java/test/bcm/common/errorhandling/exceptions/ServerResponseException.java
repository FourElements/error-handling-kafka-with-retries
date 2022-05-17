package test.bcm.common.errorhandling.exceptions;

public class ServerResponseException extends RuntimeException {

    public ServerResponseException() {
        super();
    }

    public ServerResponseException(String s) {
        super(s);
    }
}
