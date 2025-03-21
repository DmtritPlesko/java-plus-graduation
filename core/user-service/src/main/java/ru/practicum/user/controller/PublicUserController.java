package ru.practicum.user.controller;

import interaction.dto.user.UserDto;
import interaction.dto.user.UserShortDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.service.PublicUserService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class PublicUserController {

    final PublicUserService service;

    @GetMapping(path = "/{userId}")
    UserDto getById(@PathVariable("userId") Long id) {
        return service.getBy(id);
    }

    @GetMapping
    Map<Long, UserShortDto> getAllBuIds(@RequestParam List<Long> ids) {
        return service.getAllBuIds(ids);
    }
}
