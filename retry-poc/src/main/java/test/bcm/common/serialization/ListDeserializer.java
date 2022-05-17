package test.bcm.common.serialization;

import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.DoubleDeserializer;
import org.apache.kafka.common.serialization.FloatDeserializer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.ShortDeserializer;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.apache.kafka.common.utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.kafka.common.utils.Utils.mkEntry;
import static org.apache.kafka.common.utils.Utils.mkMap;

public class ListDeserializer<Inner> implements Deserializer<List<Inner>> {

    private static final Map<Class<? extends Deserializer<?>>, Integer> FIXED_LENGTH_DESERIALIZERS = mkMap(
        mkEntry(ShortDeserializer.class, Short.BYTES),
        mkEntry(IntegerDeserializer.class, Integer.BYTES),
        mkEntry(FloatDeserializer.class, Float.BYTES),
        mkEntry(LongDeserializer.class, Long.BYTES),
        mkEntry(DoubleDeserializer.class, Double.BYTES),
        mkEntry(UUIDDeserializer.class, 36)
    );

    private Deserializer<Inner> inner;
    private Class<?> listClass;
    private Integer primitiveSize;

    public ListDeserializer() {}

    public <L extends List<Inner>> ListDeserializer(Class<L> listClass, Deserializer<Inner> inner) {
        if (listClass == null || inner == null) {
            throw new IllegalArgumentException(
                "ListDeserializer requires both \"listClass\" and \"innerDeserializer\" parameters to be provided during initialization");
        }
        this.listClass = listClass;
        this.inner = inner;
        this.primitiveSize = FIXED_LENGTH_DESERIALIZERS.get(inner.getClass());
    }

    public Deserializer<Inner> innerDeserializer() {
        return inner;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        if (listClass != null || inner != null) {
            throw new ConfigException("List deserializer was already initialized using a non-default constructor");
        }
        configureListClass(configs, isKey);
        configureInnerSerde(configs, isKey);
    }

    private void configureListClass(Map<String, ?> configs, boolean isKey) {
        String listTypePropertyName = isKey ? CustomCommonClientConfigs.DEFAULT_LIST_KEY_SERDE_TYPE_CLASS :
            CustomCommonClientConfigs.DEFAULT_LIST_VALUE_SERDE_TYPE_CLASS;
        final Object listClassOrName = configs.get(listTypePropertyName);
        if (listClassOrName == null) {
            throw new ConfigException(
                "Not able to determine the list class because it was neither passed via the constructor nor set in the config.");
        }
        try {
            if (listClassOrName instanceof String) {
                listClass = Utils.loadClass((String) listClassOrName, Object.class);
            } else if (listClassOrName instanceof Class) {
                listClass = (Class<?>) listClassOrName;
            } else {
                throw new KafkaException("Could not determine the list class instance using \"" + listTypePropertyName + "\" property.");
            }
        } catch (final ClassNotFoundException e) {
            throw new ConfigException(listTypePropertyName, listClassOrName,
                                      "Deserializer's list class \"" + listClassOrName + "\" could not be found.");
        }
    }

    @SuppressWarnings("unchecked")
    private void configureInnerSerde(Map<String, ?> configs, boolean isKey) {
        String innerSerdePropertyName = isKey ? CustomCommonClientConfigs.DEFAULT_LIST_KEY_SERDE_INNER_CLASS :
            CustomCommonClientConfigs.DEFAULT_LIST_VALUE_SERDE_INNER_CLASS;
        final Object innerSerdeClassOrName = configs.get(innerSerdePropertyName);
        if (innerSerdeClassOrName == null) {
            throw new ConfigException(
                "Not able to determine the inner serde class because it was neither passed via the constructor nor set in the config.");
        }
        try {
            if (innerSerdeClassOrName instanceof String) {
                inner = Utils.newInstance((String) innerSerdeClassOrName, Serde.class).deserializer();
            } else if (innerSerdeClassOrName instanceof Class) {
                inner = (Deserializer<Inner>) ((Serde) Utils.newInstance((Class) innerSerdeClassOrName)).deserializer();
            } else {
                throw new KafkaException("Could not determine the inner serde class instance using \"" + innerSerdePropertyName + "\" property.");
            }
            inner.configure(configs, isKey);
            primitiveSize = FIXED_LENGTH_DESERIALIZERS.get(inner.getClass());
        } catch (final ClassNotFoundException e) {
            throw new ConfigException(innerSerdePropertyName, innerSerdeClassOrName,
                                      "Deserializer's inner serde class \"" + innerSerdeClassOrName + "\" could not be found.");
        }
    }

    @SuppressWarnings("unchecked")
    private List<Inner> createListInstance(int listSize) {
        try {
            Constructor<List<Inner>> listConstructor;
            try {
                listConstructor = (Constructor<List<Inner>>) listClass.getConstructor(Integer.TYPE);
                return listConstructor.newInstance(listSize);
            } catch (NoSuchMethodException e) {
                listConstructor = (Constructor<List<Inner>>) listClass.getConstructor();
                return listConstructor.newInstance();
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
            IllegalArgumentException | InvocationTargetException e) {
            throw new KafkaException("Could not construct a list instance of \"" + listClass.getCanonicalName() + "\"", e);
        }
    }

    private CustomSerdes.ListSerde.SerializationStrategy parseSerializationStrategyFlag(final int serializationStrategyFlag) throws IOException {
        if (serializationStrategyFlag < 0 || serializationStrategyFlag >= CustomSerdes.ListSerde.SerializationStrategy.VALUES.length) {
            throw new SerializationException("Invalid serialization strategy flag value");
        }
        return CustomSerdes.ListSerde.SerializationStrategy.VALUES[serializationStrategyFlag];
    }

    private List<Integer> deserializeNullIndexList(final DataInputStream dis) throws IOException {
        int nullIndexListSize = dis.readInt();
        List<Integer> nullIndexList = new ArrayList<>(nullIndexListSize);
        while (nullIndexListSize != 0) {
            nullIndexList.add(dis.readInt());
            nullIndexListSize--;
        }
        return nullIndexList;
    }

    @Override
    public List<Inner> deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try (final DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data))) {
            CustomSerdes.ListSerde.SerializationStrategy serStrategy = parseSerializationStrategyFlag(dis.readByte());
            List<Integer> nullIndexList = null;
            if (serStrategy == CustomSerdes.ListSerde.SerializationStrategy.CONSTANT_SIZE) {
                // In CONSTANT_SIZE strategy, indexes of null entries are decoded from a null index list
                nullIndexList = deserializeNullIndexList(dis);
            }
            final int size = dis.readInt();
            List<Inner> deserializedList = createListInstance(size);
            for (int i = 0; i < size; i++) {
                int entrySize = serStrategy == CustomSerdes.ListSerde.SerializationStrategy.CONSTANT_SIZE ? primitiveSize : dis.readInt();
                if (entrySize == CustomSerdes.ListSerde.NULL_ENTRY_VALUE || (nullIndexList != null && nullIndexList.contains(i))) {
                    deserializedList.add(null);
                    continue;
                }
                byte[] payload = new byte[entrySize];
                if (dis.read(payload) == -1) {
                    throw new SerializationException("End of the stream was reached prematurely");
                }
                deserializedList.add(inner.deserialize(topic, payload));
            }
            return deserializedList;
        } catch (IOException e) {
            throw new KafkaException("Unable to deserialize into a List", e);
        }
    }

    @Override
    public void close() {
        if (inner != null) {
            inner.close();
        }
    }

}
