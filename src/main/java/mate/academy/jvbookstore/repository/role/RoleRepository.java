package mate.academy.jvbookstore.repository.role;

import java.util.Optional;
import mate.academy.jvbookstore.model.Role;
import mate.academy.jvbookstore.model.Role.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface RoleRepository extends JpaRepository<Role, Long> {
    @NonNull
    Optional<Role> findByName(RoleName name);
}
