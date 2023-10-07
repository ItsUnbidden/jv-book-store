package mate.academy.jvbookstore.exception;

public class RoleAlreadyPresentException extends RuntimeException {
    public RoleAlreadyPresentException(String message) {
        super(message);
    }
}
