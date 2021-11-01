package pl.com.seremak.simplebills.endpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.com.seremak.simplebills.dto.PasswordDto;
import pl.com.seremak.simplebills.model.User;
import pl.com.seremak.simplebills.service.UserCrudService;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserCrudEndpoint {

    public static final String USER_FETCHED_MESSAGE = "User with login={} successfully fetched";
    public static final String PASSWORD_CHANGED_MESSAGE = "Password for user={} successfully changed";
    public static final String PASSWORD_CHANGE_REQUEST_MESSAGE = "Change password request received for user={}";
    public static final String GET_USER_REQUEST_RECEIVED_MESSAGE = "Get login by id request for user with login={} received";
    public static final String USER_CREATED_MESSAGE = "User={} successfully created.";
    public static final String USER_CREATION_REQUEST_RECEIVED_MESSAGE = "User creation request received for user with login={}";
    private final UserCrudService userCrudService;


    @PostMapping(value = "/admin", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> createUser(@Valid @RequestBody final User user) {
        log.info(USER_CREATION_REQUEST_RECEIVED_MESSAGE, user.getLogin());
        return userCrudService.createUser(user)
                .doOnSuccess(__ -> log.info(USER_CREATED_MESSAGE, user.getLogin()))
                .map(this::createResponse);
    }

    @GetMapping(value = "/admin/{login}", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<User>> getUserByLogin(@PathVariable final String login) {
        log.info(GET_USER_REQUEST_RECEIVED_MESSAGE, login);
        return userCrudService.getUserByLogin(login)
                .doOnSuccess(__ -> log.info(USER_FETCHED_MESSAGE, login))
                .map(ResponseEntity::ok);
    }

    @PutMapping(value = "/change-password", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Void>> changePassword(@Valid @RequestBody final PasswordDto passwordDto) {
        log.info(PASSWORD_CHANGE_REQUEST_MESSAGE, passwordDto.getUser());
        return userCrudService.changePassword(passwordDto)
                .doOnSuccess(__ -> log.info(PASSWORD_CHANGED_MESSAGE, passwordDto.getUser()))
                .map(ResponseEntity::ok);
    }

    private ResponseEntity<String> createResponse(final String id) {
        return ResponseEntity.created(URI.create(String.format("/users/%s", id)))
                .body(id);
    }
}
