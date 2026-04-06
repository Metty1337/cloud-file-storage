package metty1337.cloudfilestorage.service.impl;

import lombok.RequiredArgsConstructor;
import metty1337.cloudfilestorage.dto.request.SignUpRequest;
import metty1337.cloudfilestorage.dto.response.SignUpResponse;
import metty1337.cloudfilestorage.entity.User;
import metty1337.cloudfilestorage.exception.UserAlreadyExistException;
import metty1337.cloudfilestorage.mapper.UserMapper;
import metty1337.cloudfilestorage.repository.UserRepository;
import metty1337.cloudfilestorage.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public SignUpResponse createUser(SignUpRequest signUpRequest) {
        User userWithHashedPassword = new User(signUpRequest.username(), passwordEncoder.encode(signUpRequest.password()));

        try {
            userRepository.save(userWithHashedPassword);
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistException(e);
        }

        return userMapper.toSignUpResponse(userWithHashedPassword);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}