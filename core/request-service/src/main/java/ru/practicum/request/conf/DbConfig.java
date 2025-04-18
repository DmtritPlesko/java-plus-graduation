package ru.practicum.request.conf;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

//все стандартные методы не работают
//единственный вариант как у меня получилось сделать это вот так

@Configuration
public class DbConfig {

    @Bean
    CommandLineRunner forceCreateTable(DataSource dataSource) {
        return args -> {
            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {

                String sql = """
                CREATE TABLE IF NOT EXISTS requests (
                    id BIGSERIAL PRIMARY KEY,
                    created TIMESTAMP NOT NULL,
                    event_id BIGINT NOT NULL,
                    requester_id BIGINT NOT NULL,
                    status VARCHAR(100) NOT NULL,
                    CONSTRAINT uq_event_requester UNIQUE (event_id, requester_id)
                )
                """;

                stmt.executeUpdate(sql);
                System.out.println("✅ Таблица 'requests' создана принудительно");
            }
        };
    }
}
