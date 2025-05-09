package services.compilation.controller;

import interaction.dto.compilation.CompilationDto;
import interaction.dto.compilation.NewCompilationDto;
import interaction.dto.compilation.UpdateCompilationRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import services.compilation.service.CompilationService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationController {
    final CompilationService compilationService;

    @GetMapping("/compilations")
    public List<CompilationDto> getAll(@RequestParam(required = false) Boolean pinned,
                                       @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                       @Positive @RequestParam(required = false, defaultValue = "10") int size) {
        return compilationService.getAll(pinned, PageRequest.of(from, size));
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getBy(@PositiveOrZero @PathVariable long compId) {
        return compilationService.getBy(compId);
    }

    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto add(@Valid @RequestBody NewCompilationDto compilationDto) {
        return compilationService.add(compilationDto);
    }

    @DeleteMapping("/admin/compilations/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBy(@PathVariable("comId") long id) {
        compilationService.deleteBy(id);
    }

    @PatchMapping("/admin/compilations/{comId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateBy(@PathVariable("comId") long id,
                                   @Valid @RequestBody UpdateCompilationRequest compilationDto) {
        return compilationService.updateBy(id, compilationDto);
    }
}
