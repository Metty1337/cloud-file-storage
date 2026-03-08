package metty1337.cloudfilestorage.service;

import lombok.RequiredArgsConstructor;
import metty1337.cloudfilestorage.dto.request.SignUpRequest;
import metty1337.cloudfilestorage.dto.response.SignUpResponse;
import metty1337.cloudfilestorage.entity.User;
import metty1337.cloudfilestorage.mapper.UserMapper;
import metty1337.cloudfilestorage.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignUpResponse createUser(SignUpRequest signUpRequest) {
        User userWithHashedPassword = userMapper.toEntity(signUpRequest)
                .withPassword(passwordEncoder.encode(signUpRequest.password()));
        userRepository.save(userWithHashedPassword);
        return userMapper.toSignUpResponse(userWithHashedPassword);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
