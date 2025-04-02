package ru.practicum.user.service;

import com.querydsl.core.BooleanBuilder;
import interaction.dto.user.UserDto;
import interaction.dto.user.UserShortDto;
import interaction.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.QUser;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class PublicUserServiceImpl implements PublicUserService {

    final UserRepository repository;
    final UserMapper mapper;

    @Override
    public UserDto getBy(Long id) {
        Optional<User> userOptional = repository.findById(id);

        if (userOptional.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        return mapper.toUserDto(userOptional.get());
    }

    @Override
    public Map<Long, UserShortDto> getAllBuIds(List<Long> ids) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (ids != null && !ids.isEmpty()) {
            booleanBuilder.and(QUser.user.id.in(ids));
        }
        return StreamSupport
                .stream(repository.findAll(booleanBuilder).spliterator(), false)
                .collect(Collectors.toMap(
                        User::getId,
                        mapper::toUserShortDto
                ));
    }
}
