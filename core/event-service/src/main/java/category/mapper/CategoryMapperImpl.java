package category.mapper;

import org.springframework.stereotype.Component;
import category.model.Category;
import interaction.dto.category.CategoryDto;
import interaction.dto.category.NewCategoryDto;

@Component
public class CategoryMapperImpl implements CategoryMapper {
    @Override
    public CategoryDto toCategoryDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();

        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());

        return categoryDto;
    }

    @Override
    public Category toCategory(CategoryDto categoryDto) {

        Category category =  new Category();

        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());

        return category;
    }

    @Override
    public Category toCategory(NewCategoryDto newCategoryDto) {

        Category category = new Category();

        category.setName(newCategoryDto.getName());

        return category;
    }
}
