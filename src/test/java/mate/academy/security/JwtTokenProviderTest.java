package mate.academy.security;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import mate.academy.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

public class JwtTokenProviderTest {
    private JwtTokenProvider jwtTokenProvider;
    private UserDetailsService userDetailsService;
    private HttpServletRequest request;
    private static final String EMAIL = "bob@gmail.com";
    private static final String PASSWORD = "bobTheBest";
    private static final String USER_ROLE = "USER";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJib2"
            + "JAZ21haWwuY29tIiwicm9sZXMiOlsiVVNFU"
            + "iJdLCJpYXQiOjE2MzQyMTg2NjMsImV4cCI6MTYzNDIyMjI2M30"
            + ".9mP5p7gdfqvB2ueeQgvgVQHbMwqQkMICkf_SaWz6ZJc";
    private static final String INCORRECT_TOKEN = "eyJhbGciOiJIUzI1NiJ9"
            + ".eyJzdWIiOiJib2dsafdafafddasfdfdadffdafdfdaiOlsiVVNFU"
            + "iJdLCJpYXQiOjE2MzQxMzI2NDcsImV4cCI6MTYzNDEzNjI0N30"
            + ".uZGgd-ytnrqmOcxrmuS-mHXj_xJGcvssijQHUy-uVZM";

    @BeforeEach
    void setUp() {
        userDetailsService = Mockito.mock(UserDetailsService.class);
        jwtTokenProvider = new JwtTokenProvider(userDetailsService);
        request = Mockito.mock(HttpServletRequest.class);
        ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", "SECRET");
        ReflectionTestUtils.setField(jwtTokenProvider, "validityInMilliseconds", 3600000L);
    }

    @Test
    void createTokenMethod_ok() {
        String actual = jwtTokenProvider.createToken(EMAIL, List.of(USER_ROLE));
        Assertions.assertNotNull(actual, "Token can't be null");
        Assertions.assertEquals(3, actual.split("\\.").length,
                "Token must contain 3 parameters");
    }

    @Test
    void createTokenMethodIfGetUsernameCorrect_Ok() {
        String actualToken = jwtTokenProvider.createToken(EMAIL, List.of(USER_ROLE));
        String actual = jwtTokenProvider.getUsername(actualToken);
        Assertions.assertEquals(EMAIL, actual, "Email encoding works incorrect");
    }

    @Test
    void resolveTokenMethod_Ok() {
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer " + TOKEN);
        String actual = jwtTokenProvider.resolveToken(request);
        Assertions.assertEquals(TOKEN, actual);
    }

    @Test
    void validateTokenMethod_NotOk() {
        try {
            boolean actual = jwtTokenProvider.validateToken(INCORRECT_TOKEN);
        } catch (RuntimeException e) {
            Assertions.assertEquals("Expired or invalid JWT token", e.getMessage());
            return;
        }
        Assertions.fail("Expected to receive RuntimeException: Expired or invalid JWT token");
    }
}
