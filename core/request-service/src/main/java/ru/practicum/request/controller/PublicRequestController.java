package ru.practicum.request.controller;

import interaction.dto.request.ParticipationRequestDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.service.PublicRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicRequestController {
    final PublicRequestService requestService;

    @GetMapping()
    List<ParticipationRequestDto> getSentBy(@PathVariable long userId) {
        return requestService.getSentBy(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    ParticipationRequestDto send(@PathVariable("userId") long userId,
                                 @RequestParam("eventId") Long eventId) {
        return requestService.send(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    ParticipationRequestDto cancel(@PathVariable long userId, @PathVariable long requestId) {
        return requestService.cancel(requestId, userId);
    }
}
