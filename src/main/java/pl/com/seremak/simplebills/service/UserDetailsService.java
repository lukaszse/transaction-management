package pl.com.seremak.simplebills.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.com.seremak.simplebills.endpoint.dto.MyUserPrincipal;
import pl.com.seremak.simplebills.exceptions.NotFoundException;
import pl.com.seremak.simplebills.repository.UserCrudRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsService implements ReactiveUserDetailsService {

    public static final String USER_NOT_FOUND_ERROR_MESSAGE = "User with username=%s has been not found.";
    public static final String USER_FOUND_MESSAGE = "User with name=%s has been successfully found";
    private final UserCrudRepository userCrudRepository;

    @Override
    public Mono<UserDetails> findByUsername(final String username) {
        return userCrudRepository.findByLogin(username)
                .switchIfEmpty(Mono.error(new NotFoundException(USER_NOT_FOUND_ERROR_MESSAGE.formatted(username))))
                .map(MyUserPrincipal::new)
                .map(myUserPrincipal -> (UserDetails) myUserPrincipal)
                .doOnSuccess(user -> log.info(USER_FOUND_MESSAGE.formatted(username)));
    }
}
