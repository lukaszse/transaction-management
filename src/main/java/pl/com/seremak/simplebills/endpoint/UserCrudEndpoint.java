package pl.com.seremak.simplebills.endpoint;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.com.seremak.simplebills.model.User;
import pl.com.seremak.simplebills.service.UserCrudService;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserCrudEndpoint {

    private final UserCrudService userCrudService;

    @PostMapping
    Mono<ResponseEntity<String>> createUser(@Valid @RequestBody final User user) {
        return userCrudService.createUser(user)
                .map(this::createResponse);
    }

    @GetMapping("/{email}")
    Mono<ResponseEntity<User>> getUserByEmail(@PathVariable final String email) {
        return userCrudService.getUserByEmail(email)
                .map(ResponseEntity::ok);
    }

    private ResponseEntity<String> createResponse(final String id) {
        return ResponseEntity.created(URI.create(String.format("/users/%s", id)))
                .body(id);
    }
}
