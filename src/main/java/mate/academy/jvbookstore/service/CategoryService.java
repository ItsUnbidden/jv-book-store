package mate.academy.jvbookstore.service;

import java.util.List;
import mate.academy.jvbookstore.dto.category.CategoryDto;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

public interface CategoryService {
    CategoryDto save(@NonNull CategoryDto categoryDto);

    List<CategoryDto> findAll(@NonNull Pageable pageable);

    CategoryDto findById(@NonNull Long id);

    CategoryDto update(@NonNull Long id,
            @NonNull CategoryDto categoryDto);

    void delete(@NonNull Long id);
}
