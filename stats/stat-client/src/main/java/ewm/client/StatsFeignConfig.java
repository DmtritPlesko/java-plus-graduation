package ewm.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import feign.RequestInterceptor;
import java.time.LocalDateTime;
import java.util.Arrays;

@Configuration
public class StatsFeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            String queryLine = template.queryLine();

            String start = Arrays.stream(queryLine.split("&"))
                    .filter(p -> p.startsWith("start="))
                    .findFirst()
                    .map(p -> p.split("=")[1])
                    .orElseThrow(() -> new IllegalArgumentException("Параметр start не найден"));

            String end = Arrays.stream(queryLine.split("&"))
                    .filter(p -> p.startsWith("end="))
                    .findFirst()
                    .map(p -> p.split("=")[1])
                    .orElseThrow(() -> new IllegalArgumentException("Параметр end не найден"));

            if (LocalDateTime.parse(start).isAfter(LocalDateTime.parse(end))) {
                throw new IllegalArgumentException("Время начала не может быть позже времени окончания");
            }
        };
    }
}