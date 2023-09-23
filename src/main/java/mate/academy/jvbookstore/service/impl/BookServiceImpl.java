package mate.academy.jvbookstore.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.jvbookstore.dto.BookDto;
import mate.academy.jvbookstore.dto.BookSearchParametersDto;
import mate.academy.jvbookstore.dto.CreateBookRequestDto;
import mate.academy.jvbookstore.exception.EntityNotFoundException;
import mate.academy.jvbookstore.mapper.BookMapper;
import mate.academy.jvbookstore.model.Book;
import mate.academy.jvbookstore.repository.SpecificationBuilder;
import mate.academy.jvbookstore.repository.book.BookRepository;
import mate.academy.jvbookstore.service.BookService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository repository;

    private final BookMapper bookMapper;

    private final SpecificationBuilder<Book> specBuilder;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        return bookMapper.toDto(repository.save(bookMapper.toModel(requestDto)));
    }

    @Override
    public List<BookDto> findAll() {
        return repository.findAll().stream()
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
    public BookDto updateBook(Long id, CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        book.setId(id);
        return bookMapper.toDto(repository.save(book));
    }

    @Override
    public List<BookDto> searchBooks(BookSearchParametersDto searchParameters) {
        return repository.findAll(specBuilder.build(searchParameters)).stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
