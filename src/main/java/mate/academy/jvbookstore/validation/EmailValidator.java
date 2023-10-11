package mate.academy.jvbookstore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;

public class EmailValidator implements ConstraintValidator<Email, String> {
    @Value("${validation.email-pattern}")
    private String pattern;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return email != null && Pattern.compile(pattern).matcher(email).matches();
    }
}
