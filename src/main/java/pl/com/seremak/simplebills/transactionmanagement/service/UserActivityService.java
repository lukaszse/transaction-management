package pl.com.seremak.simplebills.transactionmanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.com.seremak.simplebills.commons.model.UserActivity;
import pl.com.seremak.simplebills.commons.utils.VersionedEntityUtils;
import pl.com.seremak.simplebills.transactionmanagement.messageQueue.MessagePublisher;
import pl.com.seremak.simplebills.transactionmanagement.repository.UserActivityRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActivityService {


    public static final String MESSAGE_TO_CATEGORY_SERVICE_IS_BEING_SENT_MSG = """
            The user is logging in for the first time. A message to category service to create standard categories set is being sent""";
    public static final String USER_EXISTS_MSG = """
            User already exists. No message to create standard categories is sent""";
    private final MessagePublisher messagePublisher;
    private final UserActivityRepository userActivityRepository;

    public Mono<UserActivity> addUserLogging(final String username) {
        final UserActivity newUserActivity = UserActivity.of(username, UserActivity.Activity.LOGGING_IN);
        final UserActivity newUserActivityToSave = VersionedEntityUtils.setMetadata(newUserActivity);
        return createCreateStandardCategories(username)
                .then(userActivityRepository.save(newUserActivityToSave));
    }

    private Mono<Boolean> createCreateStandardCategories(final String username) {
        return userActivityRepository.countByUsernameAndActivity(username, UserActivity.Activity.LOGGING_IN)
                .map(count -> count == 0)
                .doOnNext(userNotExists -> sendMessageToCategoryService(userNotExists, username))
                .doOnSuccess(UserActivityService::logInfoAboutMessageToCategoryService);
    }

    private void sendMessageToCategoryService(final boolean userNotExists, final String username) {
        if (userNotExists) {
            messagePublisher.sendUserCreationMessage(username);
        }
    }

    private static void logInfoAboutMessageToCategoryService(final boolean userNotExists) {
        if (userNotExists) {
            log.info(MESSAGE_TO_CATEGORY_SERVICE_IS_BEING_SENT_MSG);
        } else {
            log.info(USER_EXISTS_MSG);
        }
    }
}
