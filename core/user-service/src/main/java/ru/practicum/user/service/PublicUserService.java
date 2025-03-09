package ru.practicum.user.service;

import interaction.dto.user.UserDto;
import interaction.dto.user.UserShortDto;
import java.util.List;
import java.util.Map;

public interface PublicUserService {

    UserDto getBy(Long id);

    Map<Long, UserShortDto> getAllBuIds(List<Long> ids);
}
