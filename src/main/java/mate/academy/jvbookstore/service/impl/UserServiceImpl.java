package mate.academy.jvbookstore.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.jvbookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.jvbookstore.dto.user.UserResponseDto;
import mate.academy.jvbookstore.exception.EntityNotFoundException;
import mate.academy.jvbookstore.exception.RegistrationException;
import mate.academy.jvbookstore.mapper.UserMapper;
import mate.academy.jvbookstore.model.Role;
import mate.academy.jvbookstore.model.Role.RoleName;
import mate.academy.jvbookstore.model.User;
import mate.academy.jvbookstore.repository.role.RoleRepository;
import mate.academy.jvbookstore.repository.user.UserRepository;
import mate.academy.jvbookstore.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    private final UserMapper mapper;

    @Override
    public UserResponseDto registerUser(UserRegistrationRequestDto requestDto) 
            throws RegistrationException {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("User with email " 
                    + requestDto.getEmail() + " is already registred.");
        }
        Role role = roleRepository.findByName(RoleName.USER).get();
        User user = mapper.toModel(requestDto);
        user.setPassword(encoder.encode(requestDto.getPassword()));
        user.setRoles(List.of(role));
        return mapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto updateRoles(Long id, List<Role> roles) {
        User user = userRepository.findById(id).orElseThrow(() -> 
                new EntityNotFoundException("There is no registred user with id " + id));
        user.setRoles(roles);
        return mapper.toDto(userRepository.save(user));
    }

    @Override
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }
}
