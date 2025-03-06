package category.service;

import org.springframework.data.domain.Pageable;
import interaction.dto.category.CategoryDto;

import java.util.List;

public interface PublicCategoryService {
    List<CategoryDto> findAllBy(Pageable pageRequest);

    CategoryDto getBy(long id);
}
