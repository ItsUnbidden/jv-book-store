package mate.academy.jvbookstore.service.impl;

import java.util.List;
import mate.academy.jvbookstore.model.Book;
import mate.academy.jvbookstore.repository.BookRepository;
import mate.academy.jvbookstore.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository repository;
    
    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        return repository.save(book);
    }

    @Override
    public List<Book> findAll() {
        return repository.findAll();
    }
}
