package services.category.mapper;

import interaction.dto.category.CategoryDto;
import interaction.dto.category.NewCategoryDto;
import org.mapstruct.Mapping;
import services.category.model.Category;


public interface CategoryMapper {
    CategoryDto toCategoryDto(Category category);

    Category toCategory(CategoryDto categoryDto);

    @Mapping(target = "id", ignore = true)
    Category toCategory(NewCategoryDto newCategoryDto);
}
