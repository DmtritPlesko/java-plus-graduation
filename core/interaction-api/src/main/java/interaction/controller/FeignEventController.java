package interaction.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@FeignClient(name = "event")
public interface FeignEventController {

    @GetMapping(path = "/events/exist/{categoryId}")
    boolean existEventByCategoryId(@PathVariable("categoryId") Long id);

    @GetMapping(path = "/events/allin")
    List<Objects> findAllByIdIn(Set<Long> ids);

    @GetMapping
    Object findById(Long id);
}
