package mate.academy.jvbookstore.security;

import lombok.RequiredArgsConstructor;
import mate.academy.jvbookstore.dto.user.UserLoginRequestDto;
import mate.academy.jvbookstore.dto.user.UserLoginResponseDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthenticationService {
    private final AuthenticationManager authManager;

    private final JwtUtil jwtUtil;

    public UserLoginResponseDto authenticate(UserLoginRequestDto requestDto) {
        final Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                requestDto.getEmail(), requestDto.getPassword()));
                
        return new UserLoginResponseDto(jwtUtil.generateToken(authentication.getName()));
    }
}
