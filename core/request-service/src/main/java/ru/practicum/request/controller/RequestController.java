package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.service.RequestService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {

    final RequestService requestService;

    @GetMapping(path = "/confirmed")
    Map<Long, Long> getConfirmedRequestsMap(List<Long> eventIds) {
        return requestService.getConfirmedRequestsMap(eventIds);
    }

    @GetMapping(path = "/count/{id}")
    long countAllByEventIdAndStatusIs(@PathVariable("eventId") long eventId,
                                      @RequestParam RequestStatus status) {
        return requestService.countAllByEventIdAndStatusIs(eventId, status);
    }

}
