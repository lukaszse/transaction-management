package pl.com.seremak.simplebills.endpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.com.seremak.simplebills.endpoint.dto.PasswordDto;
import pl.com.seremak.simplebills.model.User;
import pl.com.seremak.simplebills.service.UserCrudService;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserCrudEndpoint {

    private final UserCrudService userCrudService;

    @PostMapping("/admin")
    public Mono<ResponseEntity<String>> createUser(@Valid @RequestBody final User user) {
        return userCrudService.createUser(user)
                .map(this::createResponse);
    }

    @GetMapping("/admin/{login}")
    public Mono<ResponseEntity<User>> getUserByLogin(@PathVariable final String login) {
        return userCrudService.getUserByLogin(login)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/change-password")
    public Mono<ResponseEntity<Void>> changePassword(@Valid @RequestBody final PasswordDto passwordDto) {
        return userCrudService.changePassword(passwordDto)
                .map(ResponseEntity::ok);
    }
    private ResponseEntity<String> createResponse(final String id) {
        return ResponseEntity.created(URI.create(String.format("/users/%s", id)))
                .body(id);
    }
}
