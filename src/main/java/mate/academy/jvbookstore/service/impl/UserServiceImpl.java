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
import mate.academy.jvbookstore.model.ShoppingCart;
import mate.academy.jvbookstore.model.User;
import mate.academy.jvbookstore.repository.role.RoleRepository;
import mate.academy.jvbookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.jvbookstore.repository.user.UserRepository;
import mate.academy.jvbookstore.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    private final ShoppingCartRepository shoppingCartRepository;

    private final UserMapper mapper;

    @Override
    public UserResponseDto registerUser(@NonNull UserRegistrationRequestDto requestDto) 
            throws RegistrationException {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("User with email " 
                    + requestDto.getEmail() + " is already registred.");
        }
        Role role = roleRepository.findByName(RoleName.USER).get();
        User user = mapper.toModel(requestDto);
        user.setPassword(encoder.encode(requestDto.getPassword()));
        user.setRoles(List.of(role));
        userRepository.save(user);
        createShoppingCartForUser(user);
        return mapper.toDto(user);
    }

    @Override
    public UserResponseDto updateRoles(@NonNull Long id, @NonNull List<Role> roles) {
        User user = userRepository.findById(id).orElseThrow(() -> 
                new EntityNotFoundException("There is no registred user with id " + id));
        user.setRoles(roles);
        return mapper.toDto(userRepository.save(user));
    }

    @Override
    public List<UserResponseDto> findAll(@NonNull Pageable pageable) {
        return userRepository.findAll(pageable).stream()
                .map(mapper::toDto)
                .toList();
    }

    private void createShoppingCartForUser(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }
}
