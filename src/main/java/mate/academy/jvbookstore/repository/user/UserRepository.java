package mate.academy.jvbookstore.repository.user;

import java.util.List;
import java.util.Optional;
import mate.academy.jvbookstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("from User u left join fetch u.roles r where u.email = :email")
    Optional<User> findByEmail(String email);

    @Query("from User u left join fetch u.roles r")
    List<User> findAll();

    @Query("from User u left join fetch u.roles r where u.id = :id")
    Optional<User> findById(Long id);
}
