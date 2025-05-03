package aggregator.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.Properties;

@Getter
@Setter
@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaConfig {

    @Value("${kafka.bootstrap-servers}")
    String server;
    @Value("${kafka.key-serializer}")
    String keySerializer;
    @Value("${kafka.value-serializer}")
    String valueSerializer;
    @Value("${kafka.key-deserializer}")
    String keyDeserializer;
    @Value("${kafka.value-deserializer}")
    String valueDeserializer;
    @Value("${kafka.consumer-group-id}")
    String consumerGroupId;
    @Value("${kafka.auto-offset-reset}")
    String autoOffsetReset;

    @Bean
    public KafkaProducer<String, Object> kafkaProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);

        return new KafkaProducer<>(props);
    }

    @Bean
    public KafkaConsumer<String, UserActionAvro> avroKafkaConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        return new KafkaConsumer<>(props);
    }
}
