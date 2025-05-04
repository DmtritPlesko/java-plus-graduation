package interaction.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "request-service", path = "/requests")
public interface FeignRequestController {
    @GetMapping(path = "/count")
    long countAllByEventIdAndStatusIs(@RequestParam("eventId") long eventId,
                                      @RequestParam String status);

    @GetMapping("/confirmed")
    Map<Long, Long> getConfirmedRequestMap(@RequestParam List<Long> eventIds);

    @GetMapping(path = "/exist/{eventId}/{userId}")
    boolean isExist(@PathVariable("eventId") Long eventId,
                    @PathVariable("userId") Long userId);
}
