package test.bcm.retrier.common.errorhandling.exceptions;

public class EntityMissingInStateStoreRetryableException extends RuntimeException {

    public EntityMissingInStateStoreRetryableException() {
        super();
    }

    public EntityMissingInStateStoreRetryableException(String s) {
        super(s);
    }
}
