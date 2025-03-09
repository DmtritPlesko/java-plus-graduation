package event.mappers;

import org.springframework.stereotype.Component;
import interaction.dto.user.NewUserRequest;
import interaction.dto.user.UserDto;
import interaction.dto.user.UserShortDto;


@Component
public class UserMapperImpl implements UserMapper {
    @Override
    public User toUser(NewUserRequest newUserRequest) {
        User user = new User();

        user.setEmail(newUserRequest.getEmail());
        user.setName(newUserRequest.getName());

        return user;
    }

    @Override
    public User toUser(UserDto userDto) {
        User user = new User();

        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());

        return user;
    }

    @Override
    public UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setId(userDto.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());

        return userDto;
    }

    @Override
    public UserShortDto toUserShortDto(User user) {
        UserShortDto userShortDto = new UserShortDto();

        userShortDto.setId(userShortDto.getId());
        userShortDto.setName(user.getName());

        return userShortDto;
    }
}
