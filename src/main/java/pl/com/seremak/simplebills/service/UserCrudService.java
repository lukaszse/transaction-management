package pl.com.seremak.simplebills.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.com.seremak.simplebills.model.User;
import pl.com.seremak.simplebills.repository.UserCrudRepository;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Slf4j
@Service
public class UserCrudService {

    private final UserCrudRepository userCrudRepository;

    UserCrudService(UserCrudRepository userCrudRepository) {
        this.userCrudRepository = userCrudRepository;
    }

    public Mono<String> createUser(final User user) {
        return userCrudRepository.save(user)
                .map(theUser -> user.getLogin())
                .doOnError(error -> log.error("Cannot create user account. Error={}", error.getMessage()));
    }

    public Mono<User> getUserByEmail(final String login) {
        return userCrudRepository.findByLogin(login)
                .doOnError(error -> log.error("Cannot find user with login={}. Error={}", login, error.getMessage()));
    }

    public Mono<Void> changePassword(final String user, final String password) {
        return Mono.empty();
    }

    private Mono<Boolean> checkIfAdminExist() {
        return userCrudRepository.findByLogin("admin")
                .map(Objects::nonNull);
    }
}
