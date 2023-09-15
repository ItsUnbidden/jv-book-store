package mate.academy.jvbookstore.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.jvbookstore.dto.BookDto;
import mate.academy.jvbookstore.dto.CreateBookRequestDto;
import mate.academy.jvbookstore.exception.EntityNotFoundException;
import mate.academy.jvbookstore.mapper.BookMapper;
import mate.academy.jvbookstore.repository.BookRepository;
import mate.academy.jvbookstore.service.BookService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository repository;

    private final BookMapper bookMapper;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        return bookMapper.toDto(repository.save(bookMapper.toModel(requestDto)));
    }

    @Override
    public List<BookDto> findAll() {
        return repository.findAll().stream().map(bookMapper::toDto).toList();
    }

    @Override
    public BookDto findById(Long id) {
        return bookMapper.toDto(repository.findById(id).orElseThrow(() -> 
                new EntityNotFoundException("Can't find book by id " + id)));
    }
}
