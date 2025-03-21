package services.event.mappers;

import interaction.dto.user.NewUserRequest;
import interaction.dto.user.UserDto;
import interaction.dto.user.UserShortDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toUser(NewUserRequest newUserRequest);

    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", ignore = true)
    UserShortDto toUserShortDto(Long id);

    UserShortDto toUserShortDto(UserDto userDto);

}
