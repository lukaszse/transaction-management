package pl.com.seremak.simplebills.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class JwtTokenParsingException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;

    public JwtTokenParsingException(final String message) {
        super(message);
    }

    public JwtTokenParsingException(final String message, Exception e) {
        super(message, e);
    }
}
