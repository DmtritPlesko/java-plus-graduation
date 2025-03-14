package compilation.service.impl;

import com.querydsl.core.BooleanBuilder;
import compilation.mappers.CompilationMapper;
import compilation.model.Compilation;
import compilation.model.QCompilation;
import compilation.repository.CompilationRepository;
import compilation.service.CompilationService;
import interaction.controller.FeignEventController;
import interaction.dto.compilation.CompilationDto;
import interaction.dto.compilation.NewCompilationDto;
import interaction.dto.compilation.UpdateCompilationRequest;
import interaction.exception.NotFoundException;
import interaction.exception.ValidationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationServiceImpl implements CompilationService {
    final CompilationRepository compilationRepository;
    final CompilationMapper compilationMapper;
    final FeignEventController feignEventController;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAll(Boolean pinned, Pageable pageRequest) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (pinned != null) {
            booleanBuilder.and(QCompilation.compilation.pinned.eq(pinned));
        }

        return compilationRepository.findAll(booleanBuilder, pageRequest)
                .stream().map(compilationMapper::toCompilationDto)
                .toList();
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
                        feignEventController.findAllByIdIn(compilationDto.getEvents()))));
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
                        feignEventController.findAllByIdIn(compilationDto.getEvents()))));
    }
}
