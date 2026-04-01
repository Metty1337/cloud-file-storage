package metty1337.cloudfilestorage.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import metty1337.cloudfilestorage.dto.request.SignInRequest;
import metty1337.cloudfilestorage.dto.request.SignUpRequest;
import metty1337.cloudfilestorage.dto.response.SignInResponse;
import metty1337.cloudfilestorage.dto.response.SignUpResponse;
import metty1337.cloudfilestorage.mapper.UserMapper;
import metty1337.cloudfilestorage.security.UserPrincipal;
import metty1337.cloudfilestorage.service.UserService;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final HttpSessionSecurityContextRepository httpSessionSecurityContextRepository;

    @Transactional
    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponse> register(@Valid @RequestBody SignUpRequest signUpRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        SignUpResponse response = userService.createUser(signUpRequest);

        Authentication authentication = getAuthentication(signUpRequest.username(), signUpRequest.password());
        saveAuthentication(httpServletRequest, httpServletResponse, authentication);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Transactional(readOnly = true)
    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> login(@Valid @RequestBody SignInRequest signInRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Authentication authentication = getAuthentication(signInRequest.username(), signInRequest.password());
        saveAuthentication(httpServletRequest, httpServletResponse, authentication);

        var userDetails = (UserPrincipal) authentication.getPrincipal();
        return new ResponseEntity<>(userMapper.toSignInResponse(userDetails), HttpStatus.OK);
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