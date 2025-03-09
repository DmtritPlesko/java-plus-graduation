package ru.practicum.request.mappers;

import interaction.dto.user.*;

public interface UserMapper {
    User toUser(NewUserRequest newUserRequest);

    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

    UserShortDto toUserShortDto(User user);
}
