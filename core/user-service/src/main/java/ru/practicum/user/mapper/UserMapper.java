package ru.practicum.user.mapper;

import interaction.dto.user.NewUserRequest;
import interaction.dto.user.UserDto;
import interaction.dto.user.UserShortDto;
import ru.practicum.user.model.User;

public interface UserMapper {
    User toUser(NewUserRequest newUserRequest);

    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

    UserShortDto toUserShortDto(User user);
}
