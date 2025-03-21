package ru.practicum.request.service.impl;

import interaction.controller.FeignEventController;
import interaction.controller.FeignUserController;
import interaction.dto.event.EventFullDto;
import interaction.dto.request.EventRequestStatusUpdateRequest;
import interaction.dto.request.EventRequestStatusUpdateResult;
import interaction.dto.request.ParticipationRequestDto;
import interaction.exception.ConflictException;
import interaction.exception.NotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.request.mappers.RequestMapper;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.request.service.PrivateRequestService;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.request.model.RequestStatus.CONFIRMED;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrivateRequestServiceImpl implements PrivateRequestService {
    final RequestRepository requestRepository;
    final FeignUserController userController;
    final RequestMapper requestMapper;
    final FeignEventController feignEventController;


    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getReceivedBy(long userId, long eventId) {
        return requestRepository.findAllByEventId(eventId).stream()
                .map(requestMapper::toParticipantRequestDto).toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult update(long userId, long eventId, EventRequestStatusUpdateRequest updateRequest) {
        userController.getBy(userId);
        EventFullDto event = feignEventController.findById(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Событие с Id = " + eventId + " не найден");
        }

        List<Long> requestsIds = updateRequest.getRequestIds();
        long confirmedRequests = requestRepository.countAllByEventIdAndStatusIs(eventId, CONFIRMED);
        List<ParticipationRequest> requests = requestRepository.findAllByIdInAndEventIdIs(requestsIds, eventId);
        long limit = event.getParticipantLimit() - confirmedRequests;
        if (limit == 0) {
            throw new ConflictException("Количество подтвержденных запросов исчерпано: " + confirmedRequests);
        }
        if (requests.size() != updateRequest.getRequestIds().size()) {
            throw new IllegalArgumentException("Не все запросы были найдены. Ошибка при вводе Ids");
        }

        List<ParticipationRequest> confirmed = new ArrayList<>();

        switch (updateRequest.getStatus()) {
            case CONFIRMED -> {
                while (limit-- > 0 && !requests.isEmpty()) {
                    ParticipationRequest request = requests.removeFirst();
                    if (request.getStatus().equals(RequestStatus.PENDING)) {
                        request.setStatus(CONFIRMED);
                        requestRepository.save(request);
                        confirmed.add(request);
                    }
                }
            }
            case REJECTED -> requests.forEach(participationRequest
                    -> participationRequest.setStatus(RequestStatus.REJECTED));
        }

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(confirmed.stream().map(requestMapper::toParticipantRequestDto).toList());
        result.setRejectedRequests(requests.stream().map(requestMapper::toParticipantRequestDto).toList());
        return result;
    }
}
