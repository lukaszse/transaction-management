package pl.com.seremak.simplebills.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import pl.com.seremak.simplebills.dto.UserDto;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtExtractionHelper {

    public static final String EXTRACTING_TOKEN_ERROR_MSG = "Error while extracting token. Reason: %s";
    private final ObjectMapper objectMapper;


    public String extractUsername(final JwtAuthenticationToken jwtAuthenticationToken) {
        return extractUser(jwtAuthenticationToken).getPreferredUsername();
    }

    public UserDto extractUser(final JwtAuthenticationToken jwtAuthenticationToken) {
        try {
            final Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
            return objectMapper.convertValue(claims, new TypeReference<>() {
            });
        } catch (final Exception e) {
            final String errorMsg = EXTRACTING_TOKEN_ERROR_MSG.formatted(e.getMessage());
            throw new AuthenticationServiceException(errorMsg);
        }
    }
}
