package mate.academy.jvbookstore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Class<?> clazz = value.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Field.setAccessible(fields, true);
        boolean isFirstValueSet = false;
        Object objToMatch = null;

        for (Field field : fields) {
            if (field.isAnnotationPresent(ApplyMatching.class)) {
                try {
                    if (!isFirstValueSet) {
                        objToMatch = field.get(value);
                        isFirstValueSet = true;
                    }
                    Object obj = field.get(value);
                    if (!(obj == null && objToMatch == null
                            || obj != null && obj.equals(objToMatch))) {
                        return false;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(String.format(
                            "Unable to get field value. Field: %s, Class: %s",
                            field, clazz.toString()), e);
                }
            }
        }
        return true;
    }
}
