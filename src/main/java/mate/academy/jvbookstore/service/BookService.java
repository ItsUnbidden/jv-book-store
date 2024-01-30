package mate.academy.jvbookstore.service;

import java.util.List;
import mate.academy.jvbookstore.dto.book.BookDto;
import mate.academy.jvbookstore.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.jvbookstore.dto.book.BookSearchParametersDto;
import mate.academy.jvbookstore.dto.book.CreateBookRequestDto;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

public interface BookService {
    BookDto save(@NonNull CreateBookRequestDto requestDto);

    List<BookDto> findAll(@NonNull Pageable pageable);

    BookDto findById(@NonNull Long id);

    void deleteById(@NonNull Long id);

    BookDto updateBook(@NonNull Long id,
            @NonNull CreateBookRequestDto requestDto);

    List<BookDto> searchBooks(@NonNull BookSearchParametersDto searchParameters,
            @NonNull Pageable pageable);

    List<BookDtoWithoutCategoryIds> findByCategoryId(@NonNull Long categoryId,
            @NonNull Pageable pageable);
}
