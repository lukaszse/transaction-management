package pl.com.seremak.simplebills.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.com.seremak.simplebills.model.User;
import reactor.core.publisher.Mono;

@Repository
public interface  UserCrudRepository extends ReactiveCrudRepository<User, String> {

    Mono<User> findByLogin(final String login);
}
