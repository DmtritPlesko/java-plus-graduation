package ru.practicum.request.service;


import ru.practicum.request.model.RequestStatus;

import java.util.List;
import java.util.Map;

public interface RequestService {

    Map<Long, Long> getConfirmedRequestsMap(List<Long> eventIds);

    long countAllByEventIdAndStatusIs(Long id, RequestStatus status);
}
