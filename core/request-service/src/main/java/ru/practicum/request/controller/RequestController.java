package ru.practicum.request.controller;

import interaction.controller.FeignRequestController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.service.RequestService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController implements FeignRequestController {

    final RequestService requestService;

    @GetMapping(path = "/count")
    @Override
    public long countAllByEventIdAndStatusIs(@RequestParam long eventId, @RequestParam String status) {
        return requestService.countAllByEventIdAndStatusIs(eventId, RequestStatus.valueOf(status));
    }

    @GetMapping(path = "/confirmed")
    @Override
    public Map<Long, Long> getConfirmedRequestMap(List<Long> eventIds) {
        return requestService.getConfirmedRequestsMap(eventIds);
    }

    @GetMapping(path = "/exist/{eventId}/{userId}")
    @Override
    public boolean isExist(@PathVariable("eventId") Long eventId,
                           @PathVariable("userId") Long userId) {
        return requestService.isExist(eventId, userId);
    }
}
