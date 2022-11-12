package pl.com.seremak.simplebills.util;

import org.springframework.http.ResponseEntity;

import java.net.URI;

public class EndpointUtils {

    public static ResponseEntity<String> prepareCreatedResponse(final String uriPattern, final String identifier) {
        return ResponseEntity.created(URI.create(String.format(uriPattern, identifier)))
                .body(identifier);
    }

    public static <T> ResponseEntity<T> prepareCreatedResponse(final String uriPattern, final String identifier, final T body) {
        return ResponseEntity.created(URI.create(String.format(uriPattern, identifier)))
                .body(body);
    }
}
