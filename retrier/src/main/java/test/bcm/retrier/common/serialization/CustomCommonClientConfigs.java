package test.bcm.retrier.common.serialization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomCommonClientConfigs {

    private static final Logger log = LoggerFactory.getLogger(CustomCommonClientConfigs.class);

    public static final String DEFAULT_LIST_KEY_SERDE_INNER_CLASS = "default.list.key.serde.inner";
    public static final String DEFAULT_LIST_KEY_SERDE_INNER_CLASS_DOC = "Default inner class of list serde for key that implements the <code>org.apache.kafka.common.serialization.Serde</code> interface. "
        + "This configuration will be read if and only if <code>default.key.serde</code> configuration is set to <code>org.apache.kafka.common.serialization.Serdes.ListSerde</code>";

    public static final String DEFAULT_LIST_VALUE_SERDE_INNER_CLASS = "default.list.value.serde.inner";
    public static final String DEFAULT_LIST_VALUE_SERDE_INNER_CLASS_DOC = "Default inner class of list serde for value that implements the <code>org.apache.kafka.common.serialization.Serde</code> interface. "
        + "This configuration will be read if and only if <code>default.value.serde</code> configuration is set to <code>org.apache.kafka.common.serialization.Serdes.ListSerde</code>";

    public static final String DEFAULT_LIST_KEY_SERDE_TYPE_CLASS = "default.list.key.serde.type";
    public static final String DEFAULT_LIST_KEY_SERDE_TYPE_CLASS_DOC = "Default class for key that implements the <code>java.util.List</code> interface. "
        + "This configuration will be read if and only if <code>default.key.serde</code> configuration is set to <code>org.apache.kafka.common.serialization.Serdes.ListSerde</code> "
        + "Note when list serde class is used, one needs to set the inner serde class that implements the <code>org.apache.kafka.common.serialization.Serde</code> interface via '"
        + DEFAULT_LIST_KEY_SERDE_INNER_CLASS + "'";

    public static final String DEFAULT_LIST_VALUE_SERDE_TYPE_CLASS = "default.list.value.serde.type";
    public static final String DEFAULT_LIST_VALUE_SERDE_TYPE_CLASS_DOC = "Default class for value that implements the <code>java.util.List</code> interface. "
        + "This configuration will be read if and only if <code>default.value.serde</code> configuration is set to <code>org.apache.kafka.common.serialization.Serdes.ListSerde</code> "
        + "Note when list serde class is used, one needs to set the inner serde class that implements the <code>org.apache.kafka.common.serialization.Serde</code> interface via '"
        + DEFAULT_LIST_VALUE_SERDE_INNER_CLASS + "'";
}
