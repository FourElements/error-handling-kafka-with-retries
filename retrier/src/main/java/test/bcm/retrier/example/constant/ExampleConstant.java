package test.bcm.retrier.example.constant;

import lombok.Getter;

public class ExampleConstant {

    public static final String REDIRECT_PERSON_IN_MEMORY_EVENT_STORE = "redirect-person";
    public static final String DEVICE_KTABLE_EVENT_STORE = "device-ktable";
    public static final String DEVICE_OUT_EVENT_STORE = "device-store";
    public static final String PERSON_IN_RETRY_COUNTER_EVENT_STORE = "person-retry-counter-store";
    public static final String ORIGIN_EVENT_KEY = "origin-event-key";
    public static final Integer MAX_KEY_RETRIES = 100;

    public enum TypeEnum {
        DEVICE("device");

        @Getter
        private final String value;

        TypeEnum(String value) {
            this.value = value;
        }

        public static TypeEnum fromValue(String value) {
            for (TypeEnum action : TypeEnum.values()) {
                if (action.getValue().equalsIgnoreCase(value)) {
                    return action;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }


}
