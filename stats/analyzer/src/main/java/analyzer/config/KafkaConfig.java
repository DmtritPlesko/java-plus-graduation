package analyzer.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.util.Properties;

@Setter
@Getter
@Configuration
@ConfigurationProperties
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaConfig {

    @Value("${kafka.bootstrap-servers}")
    String bootstrapServers;

    @Value("${kafka.key-deserializer}")
    String keyDeserializer;

    @Value("${kafka.value-deserializer-similarity}")
    String valueDeserializerSimilarity;

    @Value("${kafka.value-deserializer-actions}")
    String valueDeserializerActions;

    @Value("${kafka.consumer-analyzer-similarity-group-id}")
    String consumerAnalyzerSimilarityGroupId;

    @Value("${kafka.consumer-analyzer-actions-group-id}")
    String consumerAnalyzerActionsGroupId;

    @Value("${kafka.auto-offset-reset}")
    String autoOffsetReset;


    private Properties baseConsumer() {

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializerSimilarity);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerAnalyzerSimilarityGroupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);

        return props;
    }

    @Bean
    public KafkaConsumer<String, EventSimilarityAvro> similarityConsumer() {
        return new KafkaConsumer<>(baseConsumer());
    }

    @Bean
    public KafkaConsumer<String, EventSimilarityAvro> actionConsumer() {

        return new KafkaConsumer<>(baseConsumer());
    }

}
