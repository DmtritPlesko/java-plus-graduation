package ru.practicum.request.service;

import interaction.dto.request.EventRequestStatusUpdateRequest;
import interaction.dto.request.EventRequestStatusUpdateResult;
import interaction.dto.request.ParticipationRequestDto;

import java.util.List;

public interface PrivateRequestService {
    List<ParticipationRequestDto> getReceivedBy(long userId, long eventId);

    EventRequestStatusUpdateResult update(long userId, long eventId, EventRequestStatusUpdateRequest updateRequest);
}
