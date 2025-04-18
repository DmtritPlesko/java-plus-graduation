package ru.practicum.request;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.practicum.request.model.ParticipationRequest;

@SpringBootApplication(scanBasePackages = {"ru.practicum.request", "interaction.controller"})
@EnableDiscoveryClient
@ConfigurationPropertiesScan
@EntityScan(basePackageClasses = ParticipationRequest.class)
@EnableJpaRepositories("ru.practicum.request.repository")
@EnableFeignClients(basePackages = "interaction.controller")
public class RequestApplication {
    public static void main(String[] args) {
        SpringApplication.run(RequestApplication.class, args);
    }
}
