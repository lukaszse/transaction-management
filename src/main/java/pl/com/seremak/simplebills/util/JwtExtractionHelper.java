package pl.com.seremak.simplebills.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import pl.com.seremak.simplebills.dto.UserDto;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtExtractionHelper {

    private final ObjectMapper objectMapper;


    public String extractUsername(final JwtAuthenticationToken jwtAuthenticationToken) {
        return extractUser(jwtAuthenticationToken).getPreferredUsername();
    }

    public UserDto extractUser(final JwtAuthenticationToken jwtAuthenticationToken) {
        final Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
        return objectMapper.convertValue(claims, new TypeReference<>() {
        });
    }
}
