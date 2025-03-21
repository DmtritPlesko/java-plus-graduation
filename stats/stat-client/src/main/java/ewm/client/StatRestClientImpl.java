package ewm.client;

import ewm.dto.EndpointHitDto;
import ewm.dto.ViewStatsDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatRestClientImpl implements StatRestClient {

    final FeignStatClient statClient;
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void addHit(EndpointHitDto hitDto) {
        statClient.hit(hitDto);
    }

    public List<ViewStatsDto> stats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return statClient.stats(start.format(formatter), end.format(formatter), uris, unique);
    }
}
