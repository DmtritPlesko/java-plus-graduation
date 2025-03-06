package interaction.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import interaction.dto.request.RequestStatus;

import java.util.List;
import java.util.Map;

@FeignClient(name = "request")
public interface FeignRequestController {
    @GetMapping
    long countAllByEventIdAndStatusIs(long eventId, RequestStatus status);

    @GetMapping("/confirmed")
    Map<Long, Long> getConfirmedRequestMap(@RequestParam List<Long> eventIds);
}
