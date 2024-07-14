package Service;

public class ExceptionService extends RuntimeException {

    public ExceptionService(String message) {
        super(message);
    }

    public ExceptionService(Throwable cause) {
        super(cause);
    }

    public ExceptionService(String message, Throwable cause) {
        super(message, cause);
    }
}
