package ru.practicum.request.service.impl;

import interaction.controller.FeignEventController;
import interaction.controller.FeignUserController;
import interaction.dto.event.EventFullDto;
import interaction.dto.event.EventState;
import interaction.dto.request.ParticipationRequestDto;
import interaction.dto.user.UserDto;
import interaction.exception.ConflictException;
import interaction.exception.PermissionException;
import jakarta.ws.rs.NotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.request.mappers.RequestMapper;
import ru.practicum.request.mappers.User;
import ru.practicum.request.mappers.UserMapper;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.request.service.PublicRequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConflictException("Нельзя повторно подать заявку");
        }

        Optional<UserDto> userDto = Optional.ofNullable(feignUserController.getBy(userId));
        if (userDto.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        User requester = userMapper.toUser(userDto.get());

        Optional<EventFullDto> eventFullDto = Optional.ofNullable(feignEventController.findById(eventId));
        if (eventFullDto.isEmpty()) {
            throw new NotFoundException("Событие с id = " + eventId + " не найдено");
        }
        EventFullDto event = eventFullDto.get();

        if (requester.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("Нельзя делать запрос на свое событие");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Заявка должна быть в состоянии PUBLISHED");
        }

        boolean limit =
                event.getParticipantLimit() ==
                        requestRepository.countAllByEventIdAndStatusIs(eventId, RequestStatus.CONFIRMED)
                        && event.getParticipantLimit() != 0;

        if (limit) {
            throw new ConflictException("Нет свободных мест на мероприятие");
        }

        ParticipationRequest request = requestMapper.toParticipationRequest(event, requester);
        request.setCreated(LocalDateTime.now());

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        return requestMapper.toParticipantRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancel(long requestId, long userId) {
        feignUserController.getBy(userId);

        ParticipationRequest participationRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));

        if (userId != participationRequest.getRequesterId()) {
            throw new PermissionException("Доступ запрещен. Отменять может только владелец");
        }

        if (participationRequest.getStatus().equals(RequestStatus.PENDING)) {
            participationRequest.setStatus(RequestStatus.CANCELED);
        }

        return requestMapper.toParticipantRequestDto(participationRequest);
    }
}
