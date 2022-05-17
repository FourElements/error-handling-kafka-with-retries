package test.bcm.retrier.common.serialization;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.List;
import java.util.Map;

/**
 * Factory for creating serializers / deserializers.
 */
public class CustomSerdes {

    static public class WrapperSerde<T> implements Serde<T> {

        final private Serializer<T> serializer;
        final private Deserializer<T> deserializer;

        public WrapperSerde(Serializer<T> serializer, Deserializer<T> deserializer) {
            this.serializer = serializer;
            this.deserializer = deserializer;
        }

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {
            serializer.configure(configs, isKey);
            deserializer.configure(configs, isKey);
        }

        @Override
        public void close() {
            serializer.close();
            deserializer.close();
        }

        @Override
        public Serializer<T> serializer() {
            return serializer;
        }

        @Override
        public Deserializer<T> deserializer() {
            return deserializer;
        }
    }


    static public final class ListSerde<Inner> extends WrapperSerde<List<Inner>> {

        final static int NULL_ENTRY_VALUE = -1;


        enum SerializationStrategy {
            CONSTANT_SIZE,
            VARIABLE_SIZE;

            public static final SerializationStrategy[] VALUES = SerializationStrategy.values();
        }

        public ListSerde() {
            super(new ListSerializer<>(), new ListDeserializer<>());
        }

        public <L extends List<Inner>> ListSerde(Class<L> listClass, Serde<Inner> serde) {
            super(new ListSerializer<>(serde.serializer()), new ListDeserializer<>(listClass, serde.deserializer()));
        }

    }

    /**
     * Construct a serde object from separate serializer and deserializer
     *
     * @param serializer   must not be null.
     * @param deserializer must not be null.
     */
    static public <T> Serde<T> serdeFrom(final Serializer<T> serializer, final Deserializer<T> deserializer) {
        if (serializer == null) {
            throw new IllegalArgumentException("serializer must not be null");
        }
        if (deserializer == null) {
            throw new IllegalArgumentException("deserializer must not be null");
        }

        return new org.apache.kafka.common.serialization.Serdes.WrapperSerde<>(serializer, deserializer);
    }

    /*
     * A serde for {@code List} type
     */
    static public <L extends List<Inner>, Inner> Serde<List<Inner>> ListSerde(Class<L> listClass, Serde<Inner> innerSerde) {
        return new ListSerde<>(listClass, innerSerde);
    }

}

