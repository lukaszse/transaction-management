package pl.com.seremak.simplebills.transactionmanagement.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pl.com.seremak.simplebills.commons.model.UserActivity;
import reactor.core.publisher.Mono;

public interface UserActivityRepository extends ReactiveCrudRepository<UserActivity, String> {

    Mono<Long> countByUsernameAndActivity(final String username, final UserActivity.Activity activity);
}
