package pl.com.seremak.simplebills.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.com.seremak.simplebills.model.User;
import pl.com.seremak.simplebills.repository.UserCrudRepository;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserCrudService {

    private final UserCrudRepository userCrudRepository;

    public Mono<String> createUser(final User user) {
        return userCrudRepository.save(user)
                .map(theUser -> user.getEmail());
    }

    public Mono<User> getUserByEmail(final String email) {
        return userCrudRepository.findByEmail(email);
    }
}
