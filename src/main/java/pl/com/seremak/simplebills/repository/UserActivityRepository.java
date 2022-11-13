package pl.com.seremak.simplebills.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pl.com.seremak.simplebills.commons.model.UserActivity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserActivityRepository extends ReactiveCrudRepository<UserActivity, String> {

    Flux<UserActivity> findUserActivitiesByActivity(final UserActivity.Activity activity);

    Mono<Long> countByUsernameAndActivity(final String username, final UserActivity.Activity activity);
}
