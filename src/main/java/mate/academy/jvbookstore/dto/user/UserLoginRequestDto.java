package mate.academy.jvbookstore.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import mate.academy.jvbookstore.validation.Email;

@Data
public class UserLoginRequestDto {
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 50)
    private String password;
}
