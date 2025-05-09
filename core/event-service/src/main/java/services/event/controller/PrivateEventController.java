package services.event.controller;

import interaction.dto.event.EventFullDto;
import interaction.dto.event.EventShortDto;
import interaction.dto.event.NewEventDto;
import interaction.dto.event.UpdateEventUserRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import services.event.service.PrivateEventService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrivateEventController {
    final PrivateEventService privateEventService;

    @GetMapping
    public List<EventShortDto> getAllBy(@PathVariable long userId,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size) {
        return privateEventService.getAllBy(userId, PageRequest.of(from, size));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EventFullDto create(@PathVariable("userId") long userId,
                               @Valid @RequestBody NewEventDto newEventDto) {
        return privateEventService.create(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getBy(@PathVariable long userId, @PathVariable long eventId) {
        return privateEventService.getBy(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateBy(@PathVariable("userId") long userId, @PathVariable("eventId") long eventId,
                                 @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return privateEventService.updateBy(userId, eventId, updateEventUserRequest);
    }
}
