package interaction.controller;

import interaction.dto.user.UserDto;
import interaction.dto.user.UserShortDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "user-service")
public interface FeignUserController {

    @GetMapping(path = "/users/{userId}")
    UserDto getBy(@PathVariable("userId") long userId);

    @GetMapping(path = "/admin/users")
    List<UserDto> getAllBy(@RequestParam(required = false) List<Long> ids,
                           @RequestParam(defaultValue = "0") int from,
                           @RequestParam(defaultValue = "10") int size);

    @GetMapping(path = "/users")
    Map<Long, UserShortDto> getAllBuIds(@RequestParam List<Long> ids);
}
