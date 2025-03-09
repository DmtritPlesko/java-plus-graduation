package ru.practicum.user.service;


import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import interaction.dto.user.NewUserRequest;
import interaction.dto.user.UserDto;
import interaction.exception.NotFoundException;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {
    final UserRepository userRepository;
    final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto create(NewUserRequest newUserRequest) {
        User user = userMapper.toUser(newUserRequest);
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public List<UserDto> findAllBy(List<Long> ids, Pageable pageRequest) {

        Page<User> usersPage = userRepository.findByIds(ids, pageRequest);
        return usersPage.map(userMapper::toUserDto).toList();
    }

    @Override
    @Transactional
    public void deleteBy(long userId) {
        userRepository.deleteById(userId);
    }

    public UserDto getBy(long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с Id =" + userId + " не найден"));
    }
}
