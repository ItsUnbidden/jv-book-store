package mate.academy.jvbookstore.service;

import java.util.List;
import mate.academy.jvbookstore.dto.category.CategoryDto;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    CategoryDto save(CategoryDto categoryDto);

    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto findById(Long id);

    CategoryDto update(Long id, CategoryDto categoryDto);

    void delete(Long id);
}
