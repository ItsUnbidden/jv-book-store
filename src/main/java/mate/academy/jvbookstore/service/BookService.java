package mate.academy.jvbookstore.service;

import java.util.List;
import mate.academy.jvbookstore.dto.book.BookDto;
import mate.academy.jvbookstore.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.jvbookstore.dto.book.BookSearchParametersDto;
import mate.academy.jvbookstore.dto.book.CreateBookRequestDto;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll(Pageable pageable);

    BookDto findById(Long id);

    void deleteById(Long id);

    BookDto updateBook(Long id, CreateBookRequestDto requestDto);

    List<BookDto> searchBooks(BookSearchParametersDto searchParameters,
            Pageable pageable);

    List<BookDtoWithoutCategoryIds> findByCategoryId(Long categoryId, Pageable pageable);
}
