package services.category.service;

import interaction.dto.category.CategoryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PublicCategoryService {
    List<CategoryDto> findAllBy(Pageable pageRequest);

    CategoryDto getBy(long id);
}
