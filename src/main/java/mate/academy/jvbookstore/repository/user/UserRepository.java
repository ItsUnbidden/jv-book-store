package mate.academy.jvbookstore.repository.user;

import java.util.List;
import java.util.Optional;
import mate.academy.jvbookstore.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface UserRepository extends JpaRepository<User, Long> {
    @NonNull
    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmail(String email);

    @NonNull
    @EntityGraph(attributePaths = "roles")
    List<User> findAll();

    @NonNull
    @EntityGraph(attributePaths = "roles")
    Optional<User> findById(@NonNull Long id);
}
