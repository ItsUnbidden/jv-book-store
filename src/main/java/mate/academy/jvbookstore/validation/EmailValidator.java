package mate.academy.jvbookstore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<Email, String> {
    private static final String PATTERN = "^(?=.{1,64}@)[A-Za-z0-9_-]+"
            + "(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return email != null && Pattern.compile(PATTERN).matcher(email).matches();
    }
}
