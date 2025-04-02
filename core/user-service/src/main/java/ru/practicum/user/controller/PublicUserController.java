package ru.practicum.user.controller;

import interaction.controller.FeignUserController;
import interaction.dto.user.UserDto;
import interaction.dto.user.UserShortDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.user.service.PublicUserService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class PublicUserController implements FeignUserController {

    final PublicUserService service;

    @Override
    @GetMapping
    public UserDto getBy(@RequestParam("ids") long userId) {
        return service.getBy(userId);
    }

    @Override
    public List<UserDto> getAllBy(List<Long> ids, int from, int size) {
        return List.of();
    }

    @Override
    @GetMapping(path = "/mapping")
    public Map<Long, UserShortDto> getAllBuIds(@RequestParam List<Long> ids) {
        return service.getAllBuIds(ids);
    }
}
