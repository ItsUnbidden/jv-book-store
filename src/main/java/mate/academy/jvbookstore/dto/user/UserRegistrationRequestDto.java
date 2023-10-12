package mate.academy.jvbookstore.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import mate.academy.jvbookstore.validation.ApplyMatching;
import mate.academy.jvbookstore.validation.Email;
import mate.academy.jvbookstore.validation.FieldMatch;

@Data
@FieldMatch
public class UserRegistrationRequestDto {
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 50)
    @ApplyMatching
    private String password;

    @NotBlank
    @Size(min = 6, max = 50)
    @ApplyMatching
    private String repeatPassword;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String shippingAddress;
}
