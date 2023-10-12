package mate.academy.jvbookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.jvbookstore.dto.user.UserResponseDto;
import mate.academy.jvbookstore.model.Role;
import mate.academy.jvbookstore.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin tools for managing users")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @PatchMapping("/roles/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update roles for a specific user",
            description = "Requires ADMIN role to access",
            responses = {
                @ApiResponse(
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UserResponseDto.class)),
                    responseCode = "200",
                    description = "User with updated roles"),
                @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input"),
                @ApiResponse(
                    content = @Content(schema = @Schema(hidden = true)),
                    responseCode = "401",
                    description = "Unauthorized"), 
                @ApiResponse(
                    content = @Content(schema = @Schema(hidden = true)),
                    responseCode = "403",
                    description = "Forbidden")  
            }
    )
    public UserResponseDto updateRoles(
            @Parameter(
                description = "Id of the required user",
                required = true) 
            @PathVariable Long id, 
            @Parameter(
                description = "List of roles to apply to the user",
                required = true) 
            @RequestBody List<Role> roles) {
        return service.updateRoles(id, roles);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Find all users",
            description = "Requires ADMIN role to access",
            responses = {
                @ApiResponse(
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UserResponseDto.class)),
                    responseCode = "200",
                    description = "List of all registred users"), 
                @ApiResponse(
                    content = @Content(schema = @Schema(hidden = true)),
                    responseCode = "401",
                    description = "Unauthorized"), 
                @ApiResponse(
                    content = @Content(schema = @Schema(hidden = true)),
                    responseCode = "403",
                    description = "Forbidden")  
            }
    )
    public List<UserResponseDto> findAll(
            @Parameter(
                description = "Pagination and sorting") 
            Pageable pageable) {
        return service.findAll();
    }
}
