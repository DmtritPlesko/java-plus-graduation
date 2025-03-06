package ru.practicum.request.mappers;

import org.springframework.stereotype.Component;
import interaction.dto.request.ParticipationRequestDto;
import ru.practicum.request.model.ParticipationRequest;

@Component
public class RequestMapperImpl implements RequestMapper{
    @Override
    public ParticipationRequestDto toParticipantRequestDto(ParticipationRequest participationRequest) {

        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();

        participationRequestDto.setRequester(participationRequest.getRequester().getId());
        participationRequestDto.setId(participationRequest.getId());
        participationRequestDto.setCreated(participationRequest.getCreated());
        participationRequestDto.setStatus(participationRequestDto.getStatus());
        participationRequestDto.setEvent(participationRequestDto.getEvent());

        return participationRequestDto;
    }

    @Override
    public ParticipationRequest toParticipationRequest(Event event, User requester) {

        ParticipationRequest participationRequest = new ParticipationRequest();

        participationRequest.setRequester(requester);
        participationRequest.setEvent(event);

        return participationRequest;
    }
}
