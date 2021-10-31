package pl.com.seremak.simplebills.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.com.seremak.simplebills.model.User;
import pl.com.seremak.simplebills.service.UserCrudService;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultAdminAccountSetup {

    public static final String ADMIN = "admin";
    private final UserCrudService userCrudService;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(isAdminCreated()) {
            logAdminPassword();
        } else {
            createDefaultAdminAccount();
        }
    }

    public void createDefaultAdminAccount() {
        userCrudService.createUser(createAdmin())
                .doOnSuccess(__ -> log.info("Default admin account was created. Login: admin, password: admin"))
                .doOnError(error -> log.error("Some errors while creation initial admin account occurred. Error={}", error.getMessage()))
                .block();
    }

    public void logAdminPassword() {
        User admin = userCrudService.getUserByLogin(ADMIN).block();
        log.info("Admin account already exists. Login: {}, password: {}", admin.getLogin(), admin.getPassword());
    }

    private User createAdmin() {
        return User.builder()
                .login("admin")
                .password("admin")
                .build();
    }

    private Boolean isAdminCreated() {
        return userCrudService.getUserByLogin(ADMIN)
                .map(user -> Boolean.TRUE)
                .switchIfEmpty(Mono.just(Boolean.FALSE))
                .block();
    }

}
