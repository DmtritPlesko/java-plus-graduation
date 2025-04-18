package services.category.controller;

import interaction.dto.category.CategoryDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import services.category.service.PublicCategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicCategoryController {
    final PublicCategoryService categoryService;

    @GetMapping
    public List<CategoryDto> findAllBy(@RequestParam(defaultValue = "0") int from,
                                       @RequestParam(defaultValue = "10") int size) {
        return categoryService.findAllBy(PageRequest.of(from, size));
    }

    @GetMapping("/{catId}")
    public CategoryDto findBy(@PathVariable("catId") long catId) {
        return categoryService.getBy(catId);
    }
}
