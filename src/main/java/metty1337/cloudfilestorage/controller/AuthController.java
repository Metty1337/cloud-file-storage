package metty1337.cloudfilestorage.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import metty1337.cloudfilestorage.dto.request.SignInRequest;
import metty1337.cloudfilestorage.dto.request.SignUpRequest;
import metty1337.cloudfilestorage.dto.response.SignInResponse;
import metty1337.cloudfilestorage.dto.response.SignUpResponse;
import metty1337.cloudfilestorage.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponse> register(@Valid @RequestBody SignUpRequest signUpRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        SignUpResponse response = authService.register(signUpRequest, httpServletRequest, httpServletResponse);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> login(@Valid @RequestBody SignInRequest signInRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        SignInResponse response = authService.login(signInRequest, httpServletRequest, httpServletResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}