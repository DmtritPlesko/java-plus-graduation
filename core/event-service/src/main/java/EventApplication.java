import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"org.springframework.boot.SpringApplication;"})
public class EventApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventApplication.class,args);
    }
}
