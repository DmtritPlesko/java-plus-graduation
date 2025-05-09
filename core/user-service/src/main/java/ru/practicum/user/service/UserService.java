package ru.practicum.user.service;

import interaction.dto.user.NewUserRequest;
import interaction.dto.user.UserDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserDto create(NewUserRequest newUserRequest);

    List<UserDto> findAllBy(List<Long> ids, Pageable pageRequest);

    void deleteBy(long userId);

    UserDto getBy(long userId);
}
