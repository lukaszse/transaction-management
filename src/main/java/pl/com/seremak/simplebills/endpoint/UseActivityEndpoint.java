package pl.com.seremak.simplebills.endpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.com.seremak.simplebills.dto.UserDto;
import pl.com.seremak.simplebills.service.UserActivityService;
import pl.com.seremak.simplebills.util.JwtExtractionHelper;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UseActivityEndpoint {

    public static final String USER_INFO_FETCHED_MESSAGE = "User info for user={} successfully extracted from token.";
    public static final String USER_INFO_REQUEST_RECEIVED_MESSAGE = "Extracting information about user from jwt token with sub={}";
    public static final String USER_ACTIVITY_ADDED_MESSAGE = "User activity {} added for user {}";

    private final UserActivityService userActivityService;
    private final JwtExtractionHelper jwtExtractionHelper;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<UserDto>> findInfoAboutLoggedUser(final JwtAuthenticationToken principal) {
        log.info(USER_INFO_REQUEST_RECEIVED_MESSAGE, principal.getToken());
        final UserDto userDto = jwtExtractionHelper.extractUser(principal);
        log.info(USER_INFO_FETCHED_MESSAGE, userDto.getPreferredUsername());
        return userActivityService.addUserLogging(userDto.getPreferredUsername())
                .doOnSuccess(userActivity -> log.info(USER_ACTIVITY_ADDED_MESSAGE, userActivity.getActivity(), userActivity.getUsername()))
                .then(Mono.just(userDto))
                .map(ResponseEntity::ok);
    }
}
