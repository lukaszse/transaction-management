package pl.com.seremak.simplebills.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import pl.com.seremak.simplebills.endpoint.dto.PasswordDto;
import pl.com.seremak.simplebills.model.User;
import pl.com.seremak.simplebills.repository.UserCrudRepository;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCrudService {

    public static final String ID_FIELD = "_id";
    public static final String PASSWORD_FIELD = "password";
    public static final String USER_CREATION_ERROR_MESSAGE = "Cannot create user account. Error={}";
    public static final String USER_NOT_FIND_ERROR_MESSAGE = "Cannot find user with login={}. Error={}";
    public static final String PASSWORD_CHANGE_ERROR_MESSAGE = "Some errors while password changing occurred. Error={}";

    private final UserCrudRepository userCrudRepository;
    private final ReactiveMongoTemplate mongoTemplate;


    public Mono<String> createUser(final User user) {
        return userCrudRepository.save(user)
                .map(theUser -> user.getLogin())
                .doOnError(error -> log.error(USER_CREATION_ERROR_MESSAGE, error.getMessage()));
    }

    public Mono<User> getUserByEmail(final String login) {
        return userCrudRepository.findByLogin(login)
                .doOnError(error -> log.error(USER_NOT_FIND_ERROR_MESSAGE, login, error.getMessage()));
    }

    public Mono<Void> changePassword(final PasswordDto passwordDto) {
        return mongoTemplate.findAndModify(
                prepareFindUserQuery(passwordDto.getUser()),
                prepareSequentialIdIncrementUpdate(passwordDto.getPassword()),
                new FindAndModifyOptions().returnNew(true),
                User.class)
                .doOnError(error -> log.error(PASSWORD_CHANGE_ERROR_MESSAGE, error.getMessage()))
                .then();
    }

    private Query prepareFindUserQuery(final String user) {
        return new Query()
                .addCriteria(Criteria.where(ID_FIELD).is(user));
    }

    private Update prepareSequentialIdIncrementUpdate(final String newPassword) {
        return new Update()
                .set(PASSWORD_FIELD, newPassword);
    }
}
