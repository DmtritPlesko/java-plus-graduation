package category.service;


import interaction.dto.category.CategoryDto;
import interaction.dto.category.NewCategoryDto;

public interface AdminCategoryService {
    CategoryDto create(NewCategoryDto categoryDto);

    void deleteBy(long id);

    CategoryDto updateBy(long id, NewCategoryDto newCategoryDto);
}
