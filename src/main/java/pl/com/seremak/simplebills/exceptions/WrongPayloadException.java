package pl.com.seremak.simplebills.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class WrongPayloadException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public WrongPayloadException(final String message) {
        super(message);
    }
}
