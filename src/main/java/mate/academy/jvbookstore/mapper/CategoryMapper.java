package mate.academy.jvbookstore.mapper;

import mate.academy.jvbookstore.config.MapperConfig;
import mate.academy.jvbookstore.dto.category.CategoryDto;
import mate.academy.jvbookstore.model.Category;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toModel(CategoryDto dto);
}
