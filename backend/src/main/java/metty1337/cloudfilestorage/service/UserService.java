package metty1337.cloudfilestorage.service;

import metty1337.cloudfilestorage.dto.request.auth.SignUpRequest;
import metty1337.cloudfilestorage.dto.response.SignUpResponse;
import metty1337.cloudfilestorage.entity.User;

import java.util.Optional;

public interface UserService {
    SignUpResponse createUser(SignUpRequest signUpRequest);

    Optional<User> findByUsername(String username);
}
