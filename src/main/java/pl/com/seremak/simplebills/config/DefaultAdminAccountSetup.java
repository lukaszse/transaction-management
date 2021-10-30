package pl.com.seremak.simplebills.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.com.seremak.simplebills.model.User;
import pl.com.seremak.simplebills.repository.UserCrudRepository;
import pl.com.seremak.simplebills.service.UserCrudService;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultAdminAccountSetup {

    private final UserCrudRepository userCrudRepository;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(Objects.equals(userCrudRepository.count().block(), 0L)) {
            createDefaultAdminAccount();
        } else {
            logAdminPassword();
        }
    }

    public void createDefaultAdminAccount() {
        userCrudRepository.save(createAdmin())
                .doOnSuccess(__ -> log.info("Default admin account was created. Login: admin, password: admin"))
                .doOnError(error -> log.error("Some errors while creation initial admin account occurred. Error={}", error.getMessage()))
                .block();
    }

    public void logAdminPassword() {
        User admin = userCrudRepository.findByLogin("admin").block();
        log.info("Admin account already exists. Login: {}, password: {}", admin.getLogin(), admin.getPassword());
    }

    private User createAdmin() {
        return User.builder()
                .login("admin")
                .password("admin")
                .build();
    }

}
