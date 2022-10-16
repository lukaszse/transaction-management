package pl.com.seremak.simplebills.endpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import pl.com.seremak.simplebills.dto.UserDto;
import pl.com.seremak.simplebills.service.StatisticsService;
import pl.com.seremak.simplebills.util.JwtExtractionHelper;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserInfoEndpoint {

    public static final String USER_INFO_FETCHED_MESSAGE = "User info for user={} successfully extracted from token.";
    public static final String USER_INFO_REQUEST_RECEIVED_MESSAGE = "Extracting information about user from jwt token with sub={}";
    private final StatisticsService statisticsService;
    private final JwtExtractionHelper jwtExtractionHelper;

    @GetMapping(value = "/user-info", produces = APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<UserDto>> getInfoAboutUser(final JwtAuthenticationToken principal) {
        log.info(USER_INFO_REQUEST_RECEIVED_MESSAGE, principal.getToken());
            return Mono.just(jwtExtractionHelper.extractUser(principal))
                    .doOnSuccess(username -> log.info(USER_INFO_FETCHED_MESSAGE, username.getPreferredUsername()))
                    .map(ResponseEntity::ok);
    }
}
