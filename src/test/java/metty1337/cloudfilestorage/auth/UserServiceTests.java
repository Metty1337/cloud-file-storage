package metty1337.cloudfilestorage.auth;

import metty1337.cloudfilestorage.TestcontainersConfiguration;
import metty1337.cloudfilestorage.dto.request.SignUpRequest;
import metty1337.cloudfilestorage.dto.response.SignUpResponse;
import metty1337.cloudfilestorage.entity.User;
import metty1337.cloudfilestorage.exception.UserAlreadyExistException;
import metty1337.cloudfilestorage.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class UserServiceTests {

    @Autowired
    UserService userService;

    @Test
    @Transactional
    void shouldCreateUserAndFindByUsername() {
        SignUpRequest signUpRequest = new SignUpRequest("alex", "123");

        userService.createUser(signUpRequest);

        Optional<User> user = userService.findByUsername("alex");

        Assertions.assertTrue(user.isPresent());
    }

    @Test
    @Transactional
    void shouldThrowUserAlreadyExistException() {
        SignUpRequest signUpRequest = new SignUpRequest("alex", "123");

        userService.createUser(signUpRequest);

        Assertions.assertThrows(UserAlreadyExistException.class, () -> userService.createUser(signUpRequest));
    }

    @Test
    @Transactional
    void shouldCreateUserReturnsCorrectUsername() {
        SignUpRequest signUpRequest = new SignUpRequest("alex", "123");

        SignUpResponse response = userService.createUser(signUpRequest);

        Assertions.assertEquals("alex", response.username());
    }
}