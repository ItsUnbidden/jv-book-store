package mate.academy.jvbookstore.service;

import java.util.List;
import mate.academy.jvbookstore.model.Book;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
