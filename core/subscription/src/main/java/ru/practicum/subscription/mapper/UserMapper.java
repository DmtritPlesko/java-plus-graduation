package ru.practicum.subscription.mapper;

import interaction.dto.user.NewUserRequest;
import interaction.dto.user.UserDto;
import interaction.dto.user.UserShortDto;
import ru.practicum.subscription.model.User;

public interface UserMapper {
    User toUser(NewUserRequest newUserRequest);

    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

    UserShortDto toUserShortDto(User user);
}
