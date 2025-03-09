package interaction.controller;

import interaction.dto.user.UserDto;
import interaction.dto.user.UserShortDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "user")
public interface FeignUserController {

    @GetMapping
    UserDto getBy(long userId);

    @GetMapping
    List<UserDto> getAllBy(@RequestParam(required = false) List<Long> ids,
                            @RequestParam(defaultValue = "0") int from,
                            @RequestParam(defaultValue = "10") int size);

    @GetMapping
    Map<Long,UserShortDto> getAllBuIds(List<Long> ids);
}
