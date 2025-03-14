package category.controller;

import category.service.AdminCategoryService;
import interaction.dto.category.CategoryDto;
import interaction.dto.category.NewCategoryDto;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminCategoryController {
    final AdminCategoryService serviceAdmin;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@Valid @RequestBody NewCategoryDto categoryDto) {
        return serviceAdmin.create(categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("catId") long catId) {
        serviceAdmin.deleteBy(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateBy(@PathVariable("catId") long catId,
                                @Valid @RequestBody NewCategoryDto newCategoryDto) {
        return serviceAdmin.updateBy(catId, newCategoryDto);
    }
}
