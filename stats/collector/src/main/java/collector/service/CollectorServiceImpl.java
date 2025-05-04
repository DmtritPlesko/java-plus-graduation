package collector.service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CollectorServiceImpl implements CollectorService {

    final KafkaProducer<String, Object> kafkaProducer;

    @Value("${kafka.topic.write}")
    String topic;

    private static void onCompletion(RecordMetadata recordMetadata, Exception exception) throws KafkaException {
        if (exception == null) {
            log.info("Сообщение доставлено: {}", recordMetadata.toString());
        } else {
            throw new KafkaException(exception.toString());
        }
    }

    @Override
    public void sendAction(UserActionAvro actionAvro) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, actionAvro);
        kafkaProducer.send(record, CollectorServiceImpl::onCompletion);
    }
}
