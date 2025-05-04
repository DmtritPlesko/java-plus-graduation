package analyzer;

import analyzer.service.AnalyzerServerImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableFeignClients(basePackages = {"interaction.controller", "ewm.client"})
@ConfigurationPropertiesScan
@EnableJpaRepositories("analyzer.repository")
@EnableDiscoveryClient
@EntityScan("analyzer.model")
public class AnalyzerApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AnalyzerApplication.class, args);
        AnalyzerServerImpl analyzer = context.getBean(AnalyzerServerImpl.class);
        analyzer.start();
    }
}
