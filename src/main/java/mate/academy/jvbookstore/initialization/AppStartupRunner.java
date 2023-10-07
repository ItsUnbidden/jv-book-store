package mate.academy.jvbookstore.initialization;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.jvbookstore.model.Role;
import mate.academy.jvbookstore.model.Role.RoleName;
import mate.academy.jvbookstore.model.User;
import mate.academy.jvbookstore.repository.role.RoleRepository;
import mate.academy.jvbookstore.repository.user.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Contains methods required for initialization of basic entities like roles and the first admin.
 */
@Component
@RequiredArgsConstructor
public class AppStartupRunner implements ApplicationRunner {
    private static final String OWNER_EMAIL = "owner@bookstore.com";

    private static final String OWNER_PASSWORD = "password";

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        addRolesToDb();
        addOwnerToDb();
    }

    private void addOwnerToDb() {
        if (!userRepository.findByEmail(OWNER_EMAIL).isPresent()) {
            User owner = new User();
            owner.setEmail(OWNER_EMAIL);
            owner.setPassword(passwordEncoder.encode(OWNER_PASSWORD));
            owner.setFirstName("owner");
            owner.setLastName("owner");
            owner.setRoles(List.of(roleRepository.findByName(RoleName.ADMIN).get()));
            userRepository.save(owner);
        }
    }

    private void addRolesToDb() {
        final RoleName[] roleNames = RoleName.values();

        for (RoleName roleName : roleNames) {
            if (!roleRepository.findByName(roleName).isPresent()) {
                roleRepository.save(new Role(roleName));
            }
        }
    }
}
