package ru.practicum.request.controller;

import interaction.dto.request.EventRequestStatusUpdateRequest;
import interaction.dto.request.EventRequestStatusUpdateResult;
import interaction.dto.request.ParticipationRequestDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.service.PrivateRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/users/{userId}/events/{eventId}/requests")
public class PrivateRequestController {
    final PrivateRequestService requestService;

    @GetMapping
    List<ParticipationRequestDto> getReceivedBy(@PathVariable long userId, @PathVariable long eventId) {
        return requestService.getReceivedBy(userId, eventId);
    }

    @PatchMapping
    EventRequestStatusUpdateResult update(@PathVariable("userId") long userId, @PathVariable("eventId") long eventId,
                                          @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        return requestService.update(userId, eventId, updateRequest);
    }
}
