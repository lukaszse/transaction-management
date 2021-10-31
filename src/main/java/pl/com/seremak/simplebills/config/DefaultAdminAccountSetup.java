package pl.com.seremak.simplebills.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.com.seremak.simplebills.model.User;
import pl.com.seremak.simplebills.repository.UserCrudRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultAdminAccountSetup {

    public static final String ADMIN = "admin";
    private final UserCrudRepository userCrudRepository;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(isAdminCreated()) {
            logAdminPassword();
        } else {
            createDefaultAdminAccount();
        }
    }

    public void createDefaultAdminAccount() {
        userCrudRepository.save(createAdmin())
                .doOnSuccess(__ -> log.info("Default admin account was created. Login: admin, password: admin"))
                .doOnError(error -> log.error("Some errors while creation initial admin account occurred. Error={}", error.getMessage()))
                .block();
    }

    public void logAdminPassword() {
        User admin = userCrudRepository.findByLogin(ADMIN).block();
        log.info("Admin account already exists. Login: {}, password: {}", admin.getLogin(), admin.getPassword());
    }

    private User createAdmin() {
        return User.builder()
                .login("admin")
                .password("admin")
                .build();
    }

    private Boolean isAdminCreated() {
        return userCrudRepository.existsById(ADMIN).block();
    }

}
