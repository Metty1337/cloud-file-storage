package metty1337.cloudfilestorage.service;

import metty1337.cloudfilestorage.dto.request.SignUpRequest;
import metty1337.cloudfilestorage.dto.response.SignUpResponse;
import metty1337.cloudfilestorage.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserService {
    @Transactional
    SignUpResponse createUser(SignUpRequest signUpRequest);

    @Transactional(readOnly = true)
    Optional<User> findByUsername(String username);
}
