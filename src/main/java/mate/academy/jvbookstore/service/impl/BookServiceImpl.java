package mate.academy.jvbookstore.service.impl;

import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.jvbookstore.dto.book.BookDto;
import mate.academy.jvbookstore.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.jvbookstore.dto.book.BookSearchParametersDto;
import mate.academy.jvbookstore.dto.book.CreateBookRequestDto;
import mate.academy.jvbookstore.exception.EntityNotFoundException;
import mate.academy.jvbookstore.mapper.BookMapper;
import mate.academy.jvbookstore.model.Book;
import mate.academy.jvbookstore.model.Category;
import mate.academy.jvbookstore.repository.SpecificationBuilder;
import mate.academy.jvbookstore.repository.book.BookRepository;
import mate.academy.jvbookstore.repository.category.CategoryRepository;
import mate.academy.jvbookstore.service.BookService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository repository;

    private final BookMapper bookMapper;

    private final CategoryRepository categoryRepository;

    private final SpecificationBuilder<Book> specBuilder;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        List<Category> categoriesFromDb = requestDto.getCategoryIds().stream()
                .map(id -> categoryRepository.findById(id).orElseThrow(() -> 
                new EntityNotFoundException("There is no category with id " + id)))
                .toList();
        Book book = bookMapper.toModel(requestDto);
        book.setCategories(new HashSet<>(categoriesFromDb));
        return bookMapper.toDto(repository.save(book));
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        return repository.findAll(pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto findById(Long id) {
        return bookMapper.toDto(repository.findById(id).orElseThrow(() -> 
                new EntityNotFoundException("Can't find book by id " + id)));
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public BookDto updateBook(Long bookId, CreateBookRequestDto requestDto) {
        List<Category> categoriesFromDb = requestDto.getCategoryIds().stream()
                .map(id -> categoryRepository.findById(id).orElseThrow(() -> 
                new EntityNotFoundException("There is no category with id " + id)))
                .toList();
        Book book = bookMapper.toModel(requestDto);
        book.setId(bookId);
        book.setCategories(new HashSet<>(categoriesFromDb));
        return bookMapper.toDto(repository.save(book));
    }

    @Override
    public List<BookDto> searchBooks(BookSearchParametersDto searchParameters,
            Pageable pageable) {
        return repository.findAll(specBuilder.build(searchParameters), pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public List<BookDtoWithoutCategoryIds> findByCategoryId(Long categoryId, Pageable pageable) {
        return repository.findAllByCategoryId(categoryId, pageable).stream()
                .map(bookMapper::toBookDtoWithoutCategoryIds)
                .toList();
    }
}
