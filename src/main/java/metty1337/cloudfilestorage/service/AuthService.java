package metty1337.cloudfilestorage.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import metty1337.cloudfilestorage.dto.request.SignInRequest;
import metty1337.cloudfilestorage.dto.request.SignUpRequest;
import metty1337.cloudfilestorage.dto.response.SignInResponse;
import metty1337.cloudfilestorage.dto.response.SignUpResponse;
import metty1337.cloudfilestorage.mapper.UserMapper;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final HttpSessionSecurityContextRepository httpSessionSecurityContextRepository;

    @Transactional
    public SignUpResponse register(SignUpRequest signUpRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        SignUpResponse response = userService.createUser(signUpRequest);

        Authentication authentication = getAuthentication(signUpRequest.username(), signUpRequest.password());
        saveAuthentication(httpServletRequest, httpServletResponse, authentication);

        return response;
    }

    public SignInResponse login(SignInRequest signInRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Authentication authentication = getAuthentication(signInRequest.username(), signInRequest.password());
        saveAuthentication(httpServletRequest, httpServletResponse, authentication);

        var userDetails = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        return userMapper.toSignInResponse(userDetails);
    }

    private @NonNull Authentication getAuthentication(String username, String password) {
        var authToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authToken);
    }

    private void saveAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        httpSessionSecurityContextRepository.saveContext(context, httpServletRequest, httpServletResponse);
    }
}
