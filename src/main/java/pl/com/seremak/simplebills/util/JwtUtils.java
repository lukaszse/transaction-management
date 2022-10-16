package pl.com.seremak.simplebills.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import pl.com.seremak.simplebills.exceptions.JwtTokenParsingException;

import java.security.Principal;
import java.util.Base64;
import java.util.HashMap;

public class JwtUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Base64.Decoder decoder = Base64.getUrlDecoder();
    public static final String JWT_TOKEN_PARSING_EXCEPTION = "Error while parsing JWT token.";


    public static String extractUsername(final JwtAuthenticationToken jwtAuthenticationToken) {
        final HashMap<String, Object> tokenPayloadMap;
        try {
            final String tokenValue = jwtAuthenticationToken.getToken().getTokenValue();
            final String encodedTokenPayload = tokenValue.split("\\.")[1];
            final String decodedTokenPayload = new String(decoder.decode(encodedTokenPayload));
            tokenPayloadMap = objectMapper.readValue(decodedTokenPayload, new TypeReference<>() {
            });
            return (String) tokenPayloadMap.get("preferred_username");
        } catch (JsonProcessingException e) {
            throw new JwtTokenParsingException(JWT_TOKEN_PARSING_EXCEPTION, e);
        }
    }

}
