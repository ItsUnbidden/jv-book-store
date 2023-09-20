package mate.academy.jvbookstore.service;

import java.util.List;
import mate.academy.jvbookstore.dto.BookDto;
import mate.academy.jvbookstore.dto.CreateBookRequestDto;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll();

    BookDto findById(Long id);
}
