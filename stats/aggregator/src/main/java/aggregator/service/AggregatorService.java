package aggregator.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregatorService {

    static final Double VIEW_WEIGHT = 0.4;
    static final Double REGISTER_WEIGHT = 0.8;
    static final Double LIKE_WEIGHT = 1.0;
    final KafkaProducer<String, Object> kafkaProducer;
    final KafkaConsumer<String, UserActionAvro> kafkaConsumer;
    @Value("${kafka.topics.actions}")
    String actionTopic;
    @Value("${kafka.topics.similarity}")
    String similarityTopic;
    Map<Long, Map<Long, Double>> weights = Collections.synchronizedMap(new HashMap<>());
    Map<Long, Map<Long, Double>> minWeightsSum = Collections.synchronizedMap(new HashMap<>());

    public void start() {

        kafkaConsumer.subscribe(Collections.singleton(actionTopic));

        while (true) {
            ConsumerRecords<String, UserActionAvro> records = kafkaConsumer.poll(Duration.ofMillis(1000));

            for (ConsumerRecord<String, UserActionAvro> record : records) {

                UserActionAvro userActionAvro = record.value();
                double tempWeight = 0.0;

                switch (userActionAvro.getActionType()) {
                    case VIEW -> {
                        tempWeight = VIEW_WEIGHT;
                    }
                    case REGISTER -> {
                        tempWeight = REGISTER_WEIGHT;
                    }
                    case LIKE -> {
                        tempWeight = LIKE_WEIGHT;
                    }
                }

                double weightActionOld = 0;
                double weightActionNew = tempWeight;
                if (weights.get(userActionAvro.getEventId()) == null
                        || weights.get(userActionAvro.getEventId()).get(userActionAvro.getUserId()) == null) {

                    weights.put(userActionAvro.getEventId(), new HashMap<>());
                    weights.get(userActionAvro.getEventId()).putIfAbsent(userActionAvro.getUserId(), 0.0);

                } else {

                    weightActionNew = Math.max(weights.get(userActionAvro.getEventId())
                            .get(userActionAvro.getUserId()), tempWeight);

                    weightActionOld = weights.get(userActionAvro.getEventId()).get(userActionAvro.getUserId());

                }

                if (weightActionNew > weightActionOld) {
                    for (Long eventId : weights.keySet()) {
                        if (weights.get(eventId).get(userActionAvro.getUserId()) != null
                                && eventId != userActionAvro.getEventId()) {

                            double sumWightUsers = sumMinWeightAllUser(userActionAvro.getEventId(),eventId);
                            double sumA = sumWeightForUser(userActionAvro.getEventId());
                            double sumB = sumWeightForUser(eventId);

                            double minOld = Math.min(
                                    weightActionOld,
                                    weights.get(eventId).get(userActionAvro.getUserId()));

                            double minNew = Math.min(
                                    weightActionNew,
                                    weights.get(eventId).get(userActionAvro.getUserId()));

                            double deltaMin = minNew - minOld;
                            double sMinNew = sumWightUsers + deltaMin;

                            if (deltaMin != 0) {
                                put(userActionAvro.getEventId(), eventId, sMinNew);
                            }

                            double deltaWeightA = weightActionNew
                                    - weights.get(userActionAvro.getEventId()).get(userActionAvro.getUserId());
                            double sAnew = sumA + deltaWeightA;

                            double similarity = sMinNew / (Math.sqrt(sAnew) * Math.sqrt(sumB));

                            EventSimilarityAvro eventSimilarityAvro = new EventSimilarityAvro(
                                    eventId,
                                    userActionAvro.getEventId(),
                                    similarity,
                                    userActionAvro.getTimestamp());

                            sendEvent(eventSimilarityAvro);
                        }
                    }
                }
                weights.get(userActionAvro.getEventId()).put(userActionAvro.getUserId(), weightActionNew);
            }
            kafkaConsumer.commitAsync();
        }
    }

    private void sendEvent(EventSimilarityAvro eventSimilarity) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(similarityTopic, eventSimilarity);
        kafkaProducer.send(record, (metadata, exception) -> {
            if (exception == null) {
                log.info("Сообщение отправлено: {}", metadata.toString());
            } else {
                exception.printStackTrace();
            }
        });
    }

    private void put(long eventA, long eventB, double sum) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        minWeightsSum
                .computeIfAbsent(first, e -> new HashMap<>())
                .put(second, sum);
    }

    private Double sumMinWeightAllUser(Long eventAId, Long eventBId) {

        double sum = 0.0;

        for (Long eventId : weights.get(eventAId).keySet()) {

            double weightA = 0;
            if (weights.get(eventAId).get(eventId) != null) {
                weightA = weights.get(eventAId).get(eventId);
            }

            double weightB = 0;
            if (weights.get(eventBId).get(eventId) != null) {
                weightB = weights.get(eventBId).get(eventId);
            }

            sum += Math.min(weightA, weightB);
        }

        return sum;
    }

    private Double sumWeightForUser(Long eventId) {

        return weights.getOrDefault(eventId, Collections.emptyMap())
                .values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }

}
