package ru.practicum.request.mappers;

import interaction.dto.request.ParticipationRequestDto;
import org.springframework.stereotype.Component;
import ru.practicum.request.model.ParticipationRequest;

@Component
public class RequestMapperImpl implements RequestMapper {
    @Override
    public ParticipationRequestDto toParticipantRequestDto(ParticipationRequest participationRequest) {

        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();

        participationRequestDto.setRequester(participationRequest.getRequesterId());
        participationRequestDto.setId(participationRequest.getId());
        participationRequestDto.setCreated(participationRequest.getCreated());
        participationRequestDto.setStatus(participationRequestDto.getStatus());
        participationRequestDto.setEvent(participationRequestDto.getEvent());

        return participationRequestDto;
    }

    @Override
    public ParticipationRequest toParticipationRequest(Event event, User requester) {

        ParticipationRequest participationRequest = new ParticipationRequest();

        participationRequest.setRequesterId(requester.getId());
        participationRequest.setEventId(event.getId());

        return participationRequest;
    }
}
