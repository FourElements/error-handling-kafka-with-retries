package test.bcm.example.util;

import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.util.StringUtils;

import java.nio.ByteBuffer;

public class ExampleUtils {

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
}
