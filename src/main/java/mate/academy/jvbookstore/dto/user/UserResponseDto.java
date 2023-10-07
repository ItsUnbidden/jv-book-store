package mate.academy.jvbookstore.dto.user;

import java.util.Collection;
import lombok.Data;
import mate.academy.jvbookstore.model.Role;

@Data
public class UserResponseDto {
    private Long id;

    private String email;

    private String firstName;
    
    private String lastName;

    private String shippingAddress;

    private Collection<Role> roles;
}
