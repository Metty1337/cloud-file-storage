package metty1337.cloudfilestorage.auth;

import metty1337.cloudfilestorage.TestcontainersConfiguration;
import metty1337.cloudfilestorage.dto.request.SignInRequest;
import metty1337.cloudfilestorage.dto.request.SignUpRequest;
import metty1337.cloudfilestorage.dto.response.SignUpResponse;
import metty1337.cloudfilestorage.entity.User;
import metty1337.cloudfilestorage.exception.UserAlreadyExistException;
import metty1337.cloudfilestorage.service.AuthService;
import metty1337.cloudfilestorage.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class AuthServiceTests {

    @Autowired
    AuthService authService;

    @Autowired
    UserService userService;

    @Test
    @Transactional
    void shouldRegisterAndFindUser() {
        SignUpRequest signUpRequest = new SignUpRequest("alex", "123");
        var mockHttpServletRequest = new MockHttpServletRequest();
        var mockHttpServletResponse = new MockHttpServletResponse();

        authService.register(signUpRequest, mockHttpServletRequest, mockHttpServletResponse);

        Optional<User> user = userService.findByUsername("alex");

        Assertions.assertTrue(user.isPresent());
    }

    @Test
    @Transactional
    void shouldThrowUserAlreadyExistException() {
        SignUpRequest signUpRequest = new SignUpRequest("alex", "123");
        var mockHttpServletRequest = new MockHttpServletRequest();
        var mockHttpServletResponse = new MockHttpServletResponse();

        authService.register(signUpRequest, mockHttpServletRequest, mockHttpServletResponse);

        Assertions.assertThrows(UserAlreadyExistException.class, () -> authService.register(signUpRequest, mockHttpServletRequest, mockHttpServletResponse));
    }

    @Test
    @Transactional
    void shouldRegisterAndValidateSession() {
        SignUpRequest signUpRequest = new SignUpRequest("alex", "123");
        var mockHttpServletRequest = new MockHttpServletRequest();
        var mockHttpServletResponse = new MockHttpServletResponse();

        authService.register(signUpRequest, mockHttpServletRequest, mockHttpServletResponse);

        var session = mockHttpServletRequest.getSession(false);
        Assertions.assertNotNull(session);
        Assertions.assertNotNull(session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));
    }

    @Test
    @Transactional
    void shouldRegisterReturnsCorrectUsername() {
        SignUpRequest signUpRequest = new SignUpRequest("alex", "123");
        var mockHttpServletRequest = new MockHttpServletRequest();
        var mockHttpServletResponse = new MockHttpServletResponse();

        SignUpResponse response = authService.register(signUpRequest, mockHttpServletRequest, mockHttpServletResponse);

        Assertions.assertEquals("alex", response.username());
    }

    @Test
    @Transactional
    void shouldThrowExceptionOnLoginWithWrongPassword() {
        SignUpRequest signUpRequest = new SignUpRequest("alex", "123");
        var mockHttpServletRequest = new MockHttpServletRequest();
        var mockHttpServletResponse = new MockHttpServletResponse();
        authService.register(signUpRequest, mockHttpServletRequest, mockHttpServletResponse);

        var mockHttpServletRequestSignIn = new MockHttpServletRequest();
        var mockHttpServletResponseSignIn = new MockHttpServletResponse();
        SignInRequest signInRequest = new SignInRequest("alex", "wrongpassword");
        Assertions.assertThrows(
                AuthenticationException.class,
                () -> authService.login(signInRequest, mockHttpServletRequestSignIn, mockHttpServletResponseSignIn)
        );
    }

    @Test
    void shouldThrowExceptionOnLoginWithNonExistentUser() {
        SignInRequest signInRequest = new SignInRequest("unknown", "123");
        var mockHttpServletRequest = new MockHttpServletRequest();
        var mockHttpServletResponse = new MockHttpServletResponse();

        Assertions.assertThrows(
                AuthenticationException.class,
                () -> authService.login(signInRequest, mockHttpServletRequest, mockHttpServletResponse)
        );
    }
}
