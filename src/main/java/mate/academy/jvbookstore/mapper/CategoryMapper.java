package mate.academy.jvbookstore.mapper;

import mate.academy.jvbookstore.config.MapperConfig;
import mate.academy.jvbookstore.dto.category.CategoryDto;
import mate.academy.jvbookstore.model.Category;
import org.mapstruct.Mapper;
import org.springframework.lang.NonNull;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    @NonNull
    CategoryDto toDto(Category category);

    @NonNull
    Category toModel(CategoryDto dto);
}
