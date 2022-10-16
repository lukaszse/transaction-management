package pl.com.seremak.simplebills.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import pl.com.seremak.simplebills.dto.UserDto;
import pl.com.seremak.simplebills.exceptions.JwtTokenParsingException;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class JwtExtractionHelper {

    private final ObjectMapper objectMapper;
    private static final Base64.Decoder decoder = Base64.getUrlDecoder();
    public static final String JWT_TOKEN_PARSING_EXCEPTION = "Error while parsing JWT token.";



    public String extractUsername(final JwtAuthenticationToken jwtAuthenticationToken) {
        return extractUsername(jwtAuthenticationToken.getToken().getTokenValue());
    }

    public String extractUsername(final String jwtTokenStr) {
        return extractUser(jwtTokenStr).getPreferredUsername();
    }

    public UserDto extractUser(final JwtAuthenticationToken jwtAuthenticationToken) {
        return extractUser(jwtAuthenticationToken.getToken().getTokenValue());
    }

    public UserDto extractUser(final String jwtTokenStr) {
        try {
            final String encodedTokenPayload = jwtTokenStr.split("\\.")[1];
            final String decodedTokenPayload = new String(decoder.decode(encodedTokenPayload));
            return objectMapper.readValue(decodedTokenPayload, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new JwtTokenParsingException(JWT_TOKEN_PARSING_EXCEPTION, e);
        }
    }
}
