package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
