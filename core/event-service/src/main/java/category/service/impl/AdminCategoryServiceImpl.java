package category.service.impl;

import category.mapper.CategoryMapper;
import category.model.Category;
import category.repository.CategoryRepository;
import category.service.AdminCategoryService;
import interaction.controller.FeignEventController;
import interaction.dto.category.CategoryDto;
import interaction.dto.category.NewCategoryDto;
import interaction.exception.ConflictException;
import interaction.exception.NotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminCategoryServiceImpl implements AdminCategoryService {
    final CategoryRepository categoryRepository;
    final CategoryMapper categoryMapper;
    final FeignEventController feignEventController;

    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto categoryDto) {
        return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.toCategory(categoryDto)));
    }

    @Override
    @Transactional
    public void deleteBy(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категории с id = " + id + " не существует"));
        if (feignEventController.existEventByCategoryId(id)) {
            throw new ConflictException("Обьект имеет зависимость с событием");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CategoryDto updateBy(long id, NewCategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория не найдена"));

        category.setName(categoryDto.getName());

        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }
}

