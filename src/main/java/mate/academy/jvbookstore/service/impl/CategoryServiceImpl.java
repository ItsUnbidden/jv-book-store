package mate.academy.jvbookstore.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.jvbookstore.dto.category.CategoryDto;
import mate.academy.jvbookstore.exception.EntityNotFoundException;
import mate.academy.jvbookstore.mapper.CategoryMapper;
import mate.academy.jvbookstore.model.Category;
import mate.academy.jvbookstore.repository.category.CategoryRepository;
import mate.academy.jvbookstore.service.CategoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;

    private final CategoryMapper mapper;

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        return mapper.toDto(repository.save(mapper.toModel(categoryDto)));
    }

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return repository.findAll(pageable).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto findById(Long id) {
        return mapper.toDto(repository.findById(id).orElseThrow(() -> 
                new EntityNotFoundException("There is no category with id " + id)));
    }

    @Override
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        Category category = mapper.toModel(categoryDto);
        category.setId(id);
        return mapper.toDto(repository.save(category));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
