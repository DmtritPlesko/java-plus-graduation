package ewm;

import ewm.dto.EndpointHitDto;
import ewm.dto.RequestParamDto;
import ewm.dto.ViewStatsDto;
import ewm.service.StatService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatController {
    final StatService statService;
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        statService.hit(endpointHitDto);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsDto> stats(@RequestParam("start") String start,
                                    @RequestParam("end") String end,
                                    @RequestParam(value = "uris", required = false) List<String> uris,
                                    @RequestParam(value = "unique", required = false) boolean unique) {
        RequestParamDto requestParamDto = new RequestParamDto(LocalDateTime.parse(start, formatter),
                LocalDateTime.parse(end, formatter), uris, unique);
        return statService.stats(requestParamDto);
    }
}
