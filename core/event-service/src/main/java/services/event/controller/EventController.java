package services.event.controller;

import interaction.controller.FeignEventController;
import interaction.dto.event.EventFullDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import services.event.service.EventService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events/feign")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventController implements FeignEventController {

    final EventService service;

    @GetMapping(path = "/exist")
    @Override
    public boolean existEventByCategoryId(@RequestParam("categoryId") Long categoryId) {
        return service.existEventByCategoryId(categoryId);
    }

    @GetMapping
    @Override
    public EventFullDto findById(@RequestParam("eventId") Long eventId) {
        return service.findById(eventId);
    }
}
