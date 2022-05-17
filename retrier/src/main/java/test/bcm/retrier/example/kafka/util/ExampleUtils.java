package test.bcm.retrier.example.kafka.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.util.StringUtils;

import java.nio.ByteBuffer;

@Slf4j
public class ExampleUtils {

    public static final String RETRY_COUNTER = "retry-counter";

    private ExampleUtils() {}

    /*
        Rest proxy key comes with double quotes like "key-1" on the String itself...
     */
    public static <K> String fixRestProxyKey(K key) {
        String castedKey = (String) key;
        return StringUtils.isEmpty(castedKey) ? "null" : castedKey.replace("\"", "");
    }

    public static byte[] intToByteArray(int x) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(x);
        buffer.rewind();
        return buffer.array();
    }

    public static int byteArrayToInt(byte[] b) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.put(b);
        buffer.rewind();
        return buffer.getInt();
    }

    public static Header searchHeaderByKey(Headers headers, String key) {
        for (Header header : headers) {
            if (header.key().equalsIgnoreCase(key)) {
                return header;
            }
        }
        return null;
    }

    public static Header createRetryCountHeaderAndIncrementValue(Header header) {
        int value = ExampleUtils.byteArrayToInt(header.value());
        value += 1;
        log.info("--- Incrementing retry-counter to {}", value);
        return createRetryCountHeader(value);
    }

    public static Header createRetryCountHeader(int count) {
        return new RecordHeader(RETRY_COUNTER, ExampleUtils.intToByteArray(count));
    }
}
