package ru.practicum.request.mappers;

import interaction.dto.request.*;
import ru.practicum.request.model.ParticipationRequest;

public interface RequestMapper {

    ParticipationRequestDto toParticipantRequestDto(ParticipationRequest participationRequest);

    ParticipationRequest toParticipationRequest(Event event, User requester);
}
