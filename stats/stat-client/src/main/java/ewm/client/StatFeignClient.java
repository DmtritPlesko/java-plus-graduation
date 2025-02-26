package ewm.client;

import ewm.dto.EndpointHitDto;
import ewm.dto.ViewStatsDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "stats-server",
        configuration = StatsFeignConfig.class)
public interface StatFeignClient {
    @GetMapping("/stats")
    List<ViewStatsDto> stats(@RequestParam("start") String start,
                             @RequestParam("end") String end,
                             @RequestParam("uris") List<String> uris,
                             @RequestParam("unique") boolean unique);

    @PostMapping("/hit")
    void hit(@Valid @ModelAttribute EndpointHitDto endpointHitDto);

}
