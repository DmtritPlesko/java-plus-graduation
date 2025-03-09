package ru.practicum.request.service;


import interaction.dto.request.*;

import java.util.List;

public interface PublicRequestService {
    List<ParticipationRequestDto> getSentBy(long userId);

    ParticipationRequestDto send(long userId, long eventId);

    ParticipationRequestDto cancel(long requestId, long userId);
}
