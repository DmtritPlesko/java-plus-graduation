package analyzer.deserializer;

import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class UserAvroDeserializer implements Deserializer<UserAvroDeserializer> {
    @Override
    public UserAvroDeserializer deserialize(String topic, byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        try {
            Decoder decoder = DecoderFactory.get().binaryDecoder(bytes, null);

            SpecificDatumReader<UserAvroDeserializer> reader = new SpecificDatumReader<>(UserAvroDeserializer.class);

            return reader.read(null, decoder);
        } catch (IOException e) {
            throw new SerializationException(
                    String.format("Ошибка десериализации Avro сообщения для топика %s. Длина данных: %d байт",
                            topic, bytes.length),
                    e);
        }

    }
}
