package metty1337.cloudfilestorage.auth;

import io.minio.MinioClient;
import metty1337.cloudfilestorage.TestcontainersConfiguration;
import metty1337.cloudfilestorage.config.minio.MinioBucketInitializer;
import metty1337.cloudfilestorage.dto.request.auth.SignUpRequest;
import metty1337.cloudfilestorage.dto.response.SignUpResponse;
import metty1337.cloudfilestorage.entity.User;
import metty1337.cloudfilestorage.exception.UserAlreadyExistException;
import metty1337.cloudfilestorage.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@TestPropertySource(properties = {
        "minio.url=http://localhost:9000",
        "minio.access.name=test",
        "minio.access.secret=test1234",
        "minio.bucket.name=test-bucket"
})
class UserServiceTests {

    @MockitoBean
    MinioClient minioClient;

    @MockitoBean
    MinioBucketInitializer minioBucketInitializer;

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