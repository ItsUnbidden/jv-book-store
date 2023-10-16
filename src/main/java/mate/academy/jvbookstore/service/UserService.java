package mate.academy.jvbookstore.service;

import java.util.List;
import mate.academy.jvbookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.jvbookstore.dto.user.UserResponseDto;
import mate.academy.jvbookstore.exception.RegistrationException;
import mate.academy.jvbookstore.model.Role;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponseDto registerUser(UserRegistrationRequestDto requestDto)
             throws RegistrationException;

    UserResponseDto updateRoles(Long id, List<Role> roles);

    List<UserResponseDto> findAll(Pageable pageable);
}
