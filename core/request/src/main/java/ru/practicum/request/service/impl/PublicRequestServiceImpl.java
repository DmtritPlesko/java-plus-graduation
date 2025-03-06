package ru.practicum.request.service.impl;

import interaction.controller.FeignUserController;
import jakarta.ws.rs.NotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import interaction.controller.FeignEventController;
import interaction.dto.request.ParticipationRequestDto;
import interaction.exception.ConflictException;
import interaction.exception.PermissionException;
import ru.practicum.request.mappers.Event;
import ru.practicum.request.mappers.RequestMapper;
import ru.practicum.request.mappers.User;
import ru.practicum.request.mappers.UserMapper;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.request.service.PublicRequestService;


import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicRequestServiceImpl implements PublicRequestService {
    final RequestRepository requestRepository;
    final FeignUserController feignUserController;
    final RequestMapper requestMapper;
    final FeignEventController feignEventController;
    final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getSentBy(long userId) {
        return requestRepository.findAllByRequesterId(userId)
                .stream().map(requestMapper::toParticipantRequestDto).toList();
    }

    @Override
    public ParticipationRequestDto send(long userId, long eventId) {

        User requester = userMapper.toUser(feignUserController.getBy(userId));

        Event event = (Event) feignEventController.findById(eventId);

        if (requester.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("Нельзя делать запрос на свое событие");
        }

        throw new ConflictException("Заявка должна быть в состоянии PUBLISHED");

    }

    @Override
    public ParticipationRequestDto cancel(long requestId, long userId) {
        feignUserController.getBy(userId);

        ParticipationRequest participationRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));

        if (userId != participationRequest.getRequester().getId()) {
            throw new PermissionException("Доступ запрещен. Отменять может только владелец");
        }

        if (participationRequest.getStatus().equals(RequestStatus.PENDING)) {
            participationRequest.setStatus(RequestStatus.CANCELED);
        }

        return requestMapper.toParticipantRequestDto(participationRequest);
    }
}
