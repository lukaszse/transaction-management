package pl.com.seremak.simplebills.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.com.seremak.simplebills.dto.PasswordDto;
import pl.com.seremak.simplebills.exceptions.NotFoundException;
import pl.com.seremak.simplebills.model.Metadata;
import pl.com.seremak.simplebills.model.User;
import pl.com.seremak.simplebills.repository.UserCrudRepository;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static pl.com.seremak.simplebills.service.util.ServiceCommons.ID_FIELD;
import static pl.com.seremak.simplebills.service.util.ServiceCommons.updateMetadata;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCrudService {
    public static final String PASSWORD_FIELD = "password";
    public static final String USER_CREATION_ERROR_MESSAGE = "Cannot create user account. Error={}";
    public static final String USER_NOT_FIND_ERROR_MESSAGE = "Cannot find user with login={}. Error={}";
    public static final String PASSWORD_CHANGE_ERROR_MESSAGE = "Some errors while password changing occurred. Error={}";
    public static final String PASSWORD_NOT_MATCHING_ERROR_MESSAGE = "Password for user=%s is not matching";

    private final UserCrudRepository userCrudRepository;
    private final ReactiveMongoTemplate mongoTemplate;


    public Mono<String> createUser(final User user) {
        return userCrudRepository.save(setMetadataAndEncodePassword(user))
                .map(theUser -> user.getLogin())
                .doOnError(error -> log.error(USER_CREATION_ERROR_MESSAGE, error.getMessage()));
    }

    public Mono<User> getUserByLogin(final String login) {
        return userCrudRepository.findByLogin(login)
                .doOnError(error -> log.error(USER_NOT_FIND_ERROR_MESSAGE, login, error.getMessage()));
    }

    public Mono<Void> changePassword(final PasswordDto passwordDto) {
        return isPasswordMatching(passwordDto)
                .flatMap(isPasswordMatching -> updateIfPasswordMatching(passwordDto, isPasswordMatching));

    }

    private Mono<Void> updateIfPasswordMatching(final PasswordDto passwordDto, final boolean isPasswordMatching) {
        return isPasswordMatching ?
                updatePassword(passwordDto) :
                Mono.error(new NotFoundException(PASSWORD_NOT_MATCHING_ERROR_MESSAGE.formatted(passwordDto.getUser())));
    }

    private Mono<Void> updatePassword(final PasswordDto passwordDto) {
        return mongoTemplate.findAndModify(
                        prepareFindUserQuery(passwordDto.getUser()),
                        prepareChangePasswordUpdate(passwordDto.getNewPassword()),
                        new FindAndModifyOptions().returnNew(true),
                        User.class)
                .doOnError(error -> log.error(PASSWORD_CHANGE_ERROR_MESSAGE, error.getMessage()))
                .then();
    }

    private Query prepareFindUserQuery(final String user) {
        return new Query()
                .addCriteria(Criteria.where(ID_FIELD).is(user));
    }

    private Update prepareChangePasswordUpdate(final String newPassword) {
        Update update = new Update().set(PASSWORD_FIELD, encodePassword(newPassword));
        return updateMetadata(update);
    }

    private User setMetadataAndEncodePassword(final User user) {
        user.setPassword(encodePassword(user.getPassword()));
        user.setMetadata(
                Metadata.builder()
                        .createdAt(Instant.now())
                        .modifiedAt(Instant.now())
                        .version(1L)
                        .build());
        return user;
    }

    private Mono<Boolean> isPasswordMatching(final PasswordDto passwordDto) {
        return userCrudRepository.findByLogin(passwordDto.getUser())
                .filter(user -> passwordEncoder().matches(passwordDto.getOldPassword(), user.getPassword()))
                .map(user -> Boolean.TRUE)
                .switchIfEmpty(Mono.just(Boolean.FALSE));
    }


    private static String encodePassword(final String password) {
        return passwordEncoder().encode(password);
    }

    private static PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
