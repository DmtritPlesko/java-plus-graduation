package category.mapper;

import category.model.Category;
import interaction.dto.category.CategoryDto;
import interaction.dto.category.NewCategoryDto;
import org.mapstruct.Mapping;


public interface CategoryMapper {
    CategoryDto toCategoryDto(Category category);

    Category toCategory(CategoryDto categoryDto);

    @Mapping(target = "id", ignore = true)
    Category toCategory(NewCategoryDto newCategoryDto);
}
