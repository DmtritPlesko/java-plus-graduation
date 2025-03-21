package interaction.controller;

import interaction.dto.event.EventFullDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "event-service", path = "/events/feign")
public interface FeignEventController {

    @GetMapping(path = "/exist/{categoryId}")
    boolean existEventByCategoryId(@PathVariable("categoryId") Long id);

    @GetMapping(path = "/{eventId}")
    EventFullDto findById(@PathVariable("eventId") Long id);
}
