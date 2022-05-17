package test.bcm.common.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String server;

    //@Value("${spring.kafka.ssl.trust-store-password:}")
    //private String trustStorePwd;
    //
    //@Value("${spring.kafka.ssl.trust-store-location:}")
    //private String trustStorePath;
    //
    //@Value("${spring.kafka.properties.ssl.endpoint.identification.algorithm:}")
    //private String identificationAlgorithm;
    //
    //@Value("${spring.kafka.properties.security.protocol}")
    //private String securityProtocol;

    private Map<String, Object> getConfigs() {
        HashMap<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        configs.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, Boolean.TRUE.toString());
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        //configs.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
        //configs.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, trustStorePwd);
        //configs.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, trustStorePath.replaceFirst("file:/", ""));
        //configs.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, identificationAlgorithm);
        return configs;
    }

    private Map<String, Object> getStringSerializerConfigs() {
        HashMap<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        configs.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, Boolean.TRUE.toString());
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //configs.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
        //configs.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, trustStorePwd);
        //configs.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, trustStorePath.replaceFirst("file:/", ""));
        //configs.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, identificationAlgorithm);
        return configs;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(getConfigs());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplateObject() {
        return new KafkaTemplate<>(producerFactory(), true);
    }

    @Bean
    public ProducerFactory<String, String> producerFactoryStrings() {
        return new DefaultKafkaProducerFactory<>(getStringSerializerConfigs());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplateStrings() {
        return new KafkaTemplate<>(producerFactoryStrings(), true);
    }

    //@Bean
    //public <K, V> ProducerFactory<K, V> stringsProducerFactory() {
    //    return new DefaultKafkaProducerFactory<>(getStringSerializerConfigs());
    //}
    //
    //@Bean
    //public <K, V> KafkaTemplate<K, V> kafkaTemplateStrings() {
    //    return new KafkaTemplate<>(stringsProducerFactory(), true);
    //}
    //
    //@Bean
    //public <K, V> ProducerFactory<K, V> producerFactoryGeneric() {
    //    return new DefaultKafkaProducerFactory<>(getConfigs());
    //}
    //
    //@Bean
    //public <K, V> KafkaTemplate<K, V> kafkaTemplateJsonValue() {
    //    return new KafkaTemplate<>(producerFactoryGeneric(), true);
    //}
}
