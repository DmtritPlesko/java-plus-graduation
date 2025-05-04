package aggregator.deserializer;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Slf4j
public class UserAvroDeserializer implements Deserializer<UserActionAvro> {

    @Override
    public UserActionAvro deserialize(String topic, byte[] data) {

        if (data == null || data.length == 0) {
            log.debug("Получены нулевые или пустые данные для топика {}", topic);
            return null;
        }

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            SpecificDatumReader<UserActionAvro> reader = new SpecificDatumReader<>(UserActionAvro.class);
            Decoder decoder = DecoderFactory.get().binaryDecoder(inputStream, null);

            UserActionAvro result = reader.read(null, decoder);

            if (result == null) {
                log.warn("Десериализация вернула null для топика {}", topic);
            }

            return result;

        } catch (IOException e) {
            String errorMsg = String.format("Ошибка десериализации данных для топика %s. Длина данных: %d байт",
                    topic, data.length);
            log.error(errorMsg, e);
            throw new SerializationException(errorMsg, e);
        } catch (AvroRuntimeException e) {
            String errorMsg = String.format("Несоответствие схемы Avro при десериализации для топика %s", topic);
            log.error(errorMsg, e);
            throw new SerializationException(errorMsg, e);
        }
    }
}
