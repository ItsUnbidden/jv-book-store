package mate.academy.jvbookstore.repository;

import java.util.List;
import mate.academy.jvbookstore.model.Book;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
