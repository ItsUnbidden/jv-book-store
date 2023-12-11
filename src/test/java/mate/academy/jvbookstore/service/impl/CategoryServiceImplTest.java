package mate.academy.jvbookstore.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import mate.academy.jvbookstore.dto.category.CategoryDto;
import mate.academy.jvbookstore.exception.EntityNotFoundException;
import mate.academy.jvbookstore.mapper.CategoryMapper;
import mate.academy.jvbookstore.model.Category;
import mate.academy.jvbookstore.repository.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;
  
    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryDto dto;
    private Pageable pageable;
    
    @BeforeEach
    void initBeforeEach() {
        pageable = PageRequest.of(0, 10);

        category = new Category();
        category.setId(1L);
        category.setName("category1");

        dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());    
    }

    @Test
    void save_WithValidCategory_SavedCategory() {
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toModel(dto)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(dto);

        CategoryDto actual = categoryService.save(dto);

        assertEquals(dto, actual);
    }
    
    @Test
    void findAll_AllCategoriesWithPageable_ListOfCategories() {
        List<Category> categories = List.of(category);
        Page<Category> page = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(page);
        when(categoryMapper.toDto(category)).thenReturn(dto);

        List<CategoryDto> actual = categoryService.findAll(pageable);

        assertEquals(1, actual.size());
        assertEquals(dto, actual.get(0));
    }

    @Test
    void findById_WithCorrectId_Category() {
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(dto);

        CategoryDto actual = categoryService.findById(category.getId());

        assertEquals(dto, actual);
    }

    @Test
    void findById_WithIncorrectId_EntityNotFoundException() {
        Long invalidId = -1L;

        when(categoryRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> categoryService.findById(invalidId));
    }

    @Test
    void updateCategory_WithChangedName_UpdatedCategory() {
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toModel(dto)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(dto);

        CategoryDto actual = categoryService.update(dto.getId(), dto);

        assertEquals(dto, actual);
    }
}
