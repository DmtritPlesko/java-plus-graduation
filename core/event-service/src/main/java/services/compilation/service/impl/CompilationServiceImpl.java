package services.compilation.service.impl;

import com.querydsl.core.BooleanBuilder;
import interaction.controller.FeignUserController;
import interaction.dto.compilation.CompilationDto;
import interaction.dto.compilation.NewCompilationDto;
import interaction.dto.compilation.UpdateCompilationRequest;
import interaction.dto.event.EventShortDto;
import interaction.exception.NotFoundException;
import interaction.exception.ValidationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import services.compilation.mappers.CompilationMapper;
import services.compilation.model.Compilation;
import services.compilation.model.QCompilation;
import services.compilation.repository.CompilationRepository;
import services.compilation.service.CompilationService;
import services.event.mappers.EventMapper;
import services.event.mappers.UserMapper;
import services.event.model.Event;
import services.event.repository.EventRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationServiceImpl implements CompilationService {
    final CompilationRepository compilationRepository;
    final CompilationMapper compilationMapper;
    final EventRepository eventRepository;
    final FeignUserController feignUserController;
    final EventMapper eventMapper;
    final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAll(Boolean pinned, Pageable pageRequest) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (pinned != null) {
            booleanBuilder.and(QCompilation.compilation.pinned.eq(pinned));
        }

        Page<Compilation> compilations = compilationRepository.findAll(booleanBuilder, pageRequest);
        List<CompilationDto> compilationDtos = new ArrayList<>();

        for (Compilation compilation : compilations) {
            CompilationDto compilationDto = compilationMapper.toCompilationDto(compilation);
            List<EventShortDto> eventShortDtos = new ArrayList<>();
            for (Event event : compilation.getEvents()) {
                eventShortDtos.add(eventMapper.toEventShortDto(event,
                        userMapper.toUserShortDto(feignUserController.getBy(event.getInitiatorId()))));
            }
            compilationDto.setEvents(new HashSet<>(eventShortDtos));
            compilationDtos.add(compilationDto);
        }

        return compilationDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getBy(Long id) {
        Optional<Compilation> compilationOptional = compilationRepository.findById(id);
        if (compilationOptional.isEmpty()) {
            throw new NotFoundException("Подборка с id=" + id + " не найдена");
        }
        return compilationMapper.toCompilationDto(compilationOptional.get());
    }

    @Override
    @Transactional
    public CompilationDto add(NewCompilationDto compilationDto) {
        if (compilationDto.getTitle() == null || compilationDto.getTitle().isBlank()) {
            throw new ValidationException("Поле title не может быть пустой или состоять из пробела");
        }

        return compilationMapper.toCompilationDto(compilationRepository
                .save(compilationMapper.toCompilation(compilationDto,
                        eventRepository.findAllByIdIn(compilationDto.getEvents()))));
    }

    @Override
    @Transactional
    public void deleteBy(long id) {
        compilationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CompilationDto updateBy(long id, UpdateCompilationRequest compilationDto) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + id + " не найдено"));

        return compilationMapper.toCompilationDto(compilationRepository
                .save(compilationMapper.toUpdateCompilation(compilation, compilationDto,
                        eventRepository.findAllByIdIn(compilationDto.getEvents()))));
    }
}
