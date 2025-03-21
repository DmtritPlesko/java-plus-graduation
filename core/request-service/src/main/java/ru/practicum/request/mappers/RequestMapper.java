package ru.practicum.request.mappers;

import interaction.dto.event.EventFullDto;
import interaction.dto.request.ParticipationRequestDto;
import ru.practicum.request.model.ParticipationRequest;

public interface RequestMapper {

    ParticipationRequestDto toParticipantRequestDto(ParticipationRequest participationRequest);

    ParticipationRequest toParticipationRequest(Event event, User requester);

    ParticipationRequest toParticipationRequest(EventFullDto eventFullDto, User user);
}
