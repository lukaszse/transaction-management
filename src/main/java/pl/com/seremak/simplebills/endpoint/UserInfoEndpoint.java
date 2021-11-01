package pl.com.seremak.simplebills.endpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.com.seremak.simplebills.endpoint.dto.BillQueryParams;
import pl.com.seremak.simplebills.endpoint.dto.UserInfoDto;
import pl.com.seremak.simplebills.service.StatisticsService;
import pl.com.seremak.simplebills.service.UserCrudService;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/users-info")
@RequiredArgsConstructor
public class UserInfoEndpoint {

    public static final String USER_INFO_FETCHED_MESSAGE = "User info for user={} successfully fetched.";
    public static final String USER_INFO_REQUEST_RECEIVED_MESSAGE = "User info request received for user={}";
    private final StatisticsService statisticsService;
    private final UserCrudService userCrudService;

    @GetMapping(value = "/{userName}", produces = APPLICATION_JSON_VALUE)
    Mono<UserInfoDto> getInfoAboutUser(@PathVariable final String userName, final BillQueryParams params) {
        log.info(USER_INFO_REQUEST_RECEIVED_MESSAGE, userName);
        return userCrudService
                .getUserByLogin(userName)
                .zipWith(statisticsService.getStatisticsForUser(userName, params))
                .map(UserInfoDto::of)
                .doOnSuccess(__ -> log.info(USER_INFO_FETCHED_MESSAGE, userName));
    }
}
