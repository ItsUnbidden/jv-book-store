package mate.academy.jvbookstore.service;

import java.util.List;
import mate.academy.jvbookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.jvbookstore.dto.user.UserResponseDto;
import mate.academy.jvbookstore.exception.RegistrationException;
import mate.academy.jvbookstore.model.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

public interface UserService {
    UserResponseDto registerUser(@NonNull UserRegistrationRequestDto requestDto)
             throws RegistrationException;

    UserResponseDto updateRoles(@NonNull Long id,
            @NonNull List<Role> roles);

    List<UserResponseDto> findAll(@NonNull Pageable pageable);
}
