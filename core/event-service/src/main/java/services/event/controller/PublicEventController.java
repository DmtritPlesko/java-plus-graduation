package services.event.controller;

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

    @GetMapping
    public List<EventShortDto> getAllBy(@Valid @ModelAttribute PublicEventParam publicEventParam,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size,
                                        HttpServletRequest request) {
        return publicEventService.getAllBy(publicEventParam, PageRequest.of(from, size));
    }

    @GetMapping("/{eventId}")
    public EventFullDto getBy(@PathVariable("eventId") long eventId,
                              HttpServletRequest request,
                              @RequestHeader("X-EWM-USER-ID") long userId) {
        return publicEventService.getBy(eventId);
    }

    @GetMapping("/recommendations")
    public List<EventFullDto> getRecommendations(HttpServletRequest request,
                                                 @RequestHeader("X-EWM-USER-ID") long userId,
                                                 @RequestParam("maxResults") int maxResults) {
        return publicEventService.getRecommendations(userId, maxResults);
    }

    @PutMapping("/{eventId}/like")
    public void like(@PathVariable("eventId") long eventId,
                     HttpServletRequest request,
                     @RequestHeader("X-EWM-USER-ID") long userId) {
        publicEventService.like(eventId, userId);
    }

    @GetMapping(path = "/exist/{categoryId}")
    public boolean existEventByCategoryId(@PathVariable("categoryId") Long id) {
        return publicEventService.exists(id);
    }

    @GetMapping(path = "/allin")
    public List<Event> findAllByIn(@RequestParam Set<Long> ids) {
        return publicEventService.findAllByIn(ids);
    }
}
