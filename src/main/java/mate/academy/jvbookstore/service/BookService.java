package mate.academy.jvbookstore.service;

import java.util.List;
import mate.academy.jvbookstore.dto.BookDto;
import mate.academy.jvbookstore.dto.BookSearchParametersDto;
import mate.academy.jvbookstore.dto.CreateBookRequestDto;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll(Pageable pageable);

    BookDto findById(Long id);

    void deleteById(Long id);

    BookDto updateBook(Long id, CreateBookRequestDto requestDto);

    List<BookDto> searchBooks(BookSearchParametersDto searchParameters,
            Pageable pageable);
}
