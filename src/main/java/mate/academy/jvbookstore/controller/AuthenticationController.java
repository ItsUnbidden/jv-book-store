package mate.academy.jvbookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.jvbookstore.dto.user.UserLoginRequestDto;
import mate.academy.jvbookstore.dto.user.UserLoginResponseDto;
import mate.academy.jvbookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.jvbookstore.dto.user.UserResponseDto;
import mate.academy.jvbookstore.exception.RegistrationException;
import mate.academy.jvbookstore.security.AuthenticationService;
import mate.academy.jvbookstore.service.UserService;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;

    private final AuthenticationService authService;

    @PostMapping("/login")
    @Operation(
            summary = "Login",
            description = "Public endpoint",
            responses = {
                @ApiResponse(
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UserLoginResponseDto.class)),
                    responseCode = "200",
                    description = "JWT Token"),
                @ApiResponse(
                    responseCode = "400",
                    description = "Provided credentials have invalid format"),
                @ApiResponse(
                    content = @Content(schema = @Schema(hidden = true)),
                    responseCode = "401",
                    description = "Authentication failed")
            }
    )
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authService.authenticate(requestDto);
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register",
            description = "Public endpoint",
            responses = {
                @ApiResponse(
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UserResponseDto.class)),
                    responseCode = "200",
                    description = "Registred user"),
                @ApiResponse(
                    responseCode = "400",
                    description = "Provided credentials have invalid format")
            }
    )
    public UserResponseDto register(@RequestBody @Valid @NonNull
            UserRegistrationRequestDto requestDto) 
            throws RegistrationException {
        return userService.registerUser(requestDto);
    }
}
