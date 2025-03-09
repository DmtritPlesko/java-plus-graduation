package ru.practicum.request.service;

import java.util.List;
import java.util.Map;

public interface RequestService {

    Map<Long, Long> getConfirmedRequestsMap(List<Long> eventIds);
}
