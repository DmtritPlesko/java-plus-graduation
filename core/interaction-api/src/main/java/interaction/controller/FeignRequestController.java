package interaction.controller;

import interaction.dto.request.RequestStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "request-service")
public interface FeignRequestController {
    @GetMapping(path = "/requests/count/{eventID}")
    long countAllByEventIdAndStatusIs(@PathVariable("eventId") long eventId,
                                      @RequestParam RequestStatus status);

    @GetMapping("/requests/confirmed")
    Map<Long, Long> getConfirmedRequestMap(@RequestParam List<Long> eventIds);
}
