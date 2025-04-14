package serializer;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CollectorSerializer implements Serializer<SpecificRecordBase> {

    final EncoderFactory encoderFactory = EncoderFactory.get();
    BinaryEncoder encoder;

    @Override
    public byte[] serialize(String topic, SpecificRecordBase specificRecordBase) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] result = null;
            encoder = encoderFactory.binaryEncoder(out, encoder);
            if (specificRecordBase != null) {
                DatumWriter<SpecificRecordBase> writer = new SpecificDatumWriter<>(specificRecordBase.getSchema());
                writer.write(specificRecordBase, encoder);
                encoder.flush();
                result = out.toByteArray();
            }
            return result;
        } catch (IOException ex) {
            throw new SerializationException("Ошибка сериализации данных для топика [" + topic + "]", ex);
        }
    }
}
