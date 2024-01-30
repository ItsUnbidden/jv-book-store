package mate.academy.jvbookstore.repository.book;

import java.util.List;
import java.util.Optional;
import mate.academy.jvbookstore.model.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

public interface BookRepository extends JpaRepository<Book, Long>,
                                        JpaSpecificationExecutor<Book> {
    @NonNull
    @Query("from Book b left join fetch b.categories c where c.id = :categoryId")
    List<Book> findAllByCategoryId(Long categoryId, Pageable pageable);

    @NonNull
    @EntityGraph(attributePaths = "categories")
    List<Book> findAll();

    @NonNull
    @EntityGraph(attributePaths = "categories")
    Optional<Book> findById(@NonNull Long id);
}
