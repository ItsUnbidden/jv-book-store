package mate.academy.jvbookstore.service;

import java.util.List;
import mate.academy.jvbookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.jvbookstore.dto.user.UserResponseDto;
import mate.academy.jvbookstore.exception.RegistrationException;

public interface UserService {
    UserResponseDto registerUser(UserRegistrationRequestDto requestDto)
             throws RegistrationException;

    UserResponseDto giveAdminRights(Long id);

    List<UserResponseDto> findAll();
}
