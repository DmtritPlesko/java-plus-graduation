package interaction.controller;

import interaction.dto.event.EventFullDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

@FeignClient(name = "event-service", path = "/events/feign")
public interface FeignEventController {

    @GetMapping(path = "/exist")
    boolean existEventByCategoryId(@RequestParam("categoryId") Long id);

    @GetMapping
    EventFullDto findById(@RequestParam("eventId") Long eventId);

    @GetMapping(path = "/events")
    List<EventFullDto> findEventsByIds(@RequestParam Set<Long> ids);

}
