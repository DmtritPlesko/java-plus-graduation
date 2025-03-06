package interaction.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import interaction.dto.user.UserDto;

@FeignClient(name = "user")
public interface FeignUserController {

    @GetMapping
    UserDto getBy(long userId);
}
