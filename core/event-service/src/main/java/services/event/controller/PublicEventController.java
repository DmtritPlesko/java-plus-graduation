package services.event.controller;

import ewm.client.StatRestClient;
import ewm.dto.EndpointHitDto;
import interaction.dto.event.EventFullDto;
import interaction.dto.event.EventShortDto;
import interaction.dto.event.PublicEventParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import services.event.model.Event;
import services.event.service.PublicEventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicEventController {
    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    final PublicEventService publicEventService;
    final StatRestClient statRestClient;

    @GetMapping
    List<EventShortDto> getAllBy(@Valid @ModelAttribute PublicEventParam publicEventParam,
                                 @RequestParam(defaultValue = "0") int from,
                                 @RequestParam(defaultValue = "10") int size,
                                 HttpServletRequest request) {
        List<EventShortDto> events = publicEventService.getAllBy(publicEventParam, PageRequest.of(from, size));
        addHit("/events", request.getRemoteAddr());
        return events;
    }

    @GetMapping("/{eventId}")
    EventFullDto getBy(@PathVariable long eventId, HttpServletRequest request) {
        EventFullDto event = publicEventService.getBy(eventId);
        addHit("/events/" + eventId, request.getRemoteAddr());
        return event;
    }

    void addHit(String uri, String ip) {
        LocalDateTime now = LocalDateTime.now();
        EndpointHitDto hitDto = new EndpointHitDto("main-server", uri, ip, now.format(dateTimeFormatter));
        statRestClient.addHit(hitDto);
    }

    @GetMapping(path = "/exist/{categoryId}")
    boolean existEventByCategoryId(@PathVariable("categoryId") Long id) {
        return publicEventService.exists(id);
    }

    @GetMapping(path = "/allin")
    List<Event> findAllByIn(@RequestParam Set<Long> ids) {
        return publicEventService.findAllByIn(ids);
    }
}
