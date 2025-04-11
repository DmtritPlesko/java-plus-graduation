package ewm.client;

import ewm.dto.EndpointHitDto;
import ewm.dto.ViewStatsDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "stats-server")
public interface FeignStatClient {
    @PostMapping("/hit")
    void hit(@Valid @RequestBody EndpointHitDto endpointHitDto);

    @GetMapping("/stats")
    List<ViewStatsDto> stats(@RequestParam(value = "start", required = false) String start,
                             @RequestParam(value = "end", required = false) String end,
                             @RequestParam(value = "uris", required = false) List<String> uris,
                             @RequestParam(value = "unique", required = false) boolean unique);

}
