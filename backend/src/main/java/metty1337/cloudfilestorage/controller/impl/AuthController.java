package metty1337.cloudfilestorage.controller.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import metty1337.cloudfilestorage.controller.AuthControllerApi;
import metty1337.cloudfilestorage.dto.request.SignInRequest;
import metty1337.cloudfilestorage.dto.request.SignUpRequest;
import metty1337.cloudfilestorage.dto.response.SignInResponse;
import metty1337.cloudfilestorage.dto.response.SignUpResponse;
import metty1337.cloudfilestorage.mapper.UserMapper;
import metty1337.cloudfilestorage.security.UserPrincipal;
import metty1337.cloudfilestorage.service.AuthFacade;
import metty1337.cloudfilestorage.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerApi {

    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthFacade authFacade;

    @PostMapping("/sign-up")
    @Override
    public ResponseEntity<SignUpResponse> register(@Valid @RequestBody SignUpRequest request,
                                                   HttpServletRequest httpRequest,
                                                   HttpServletResponse httpResponse) {
        SignUpResponse response = userService.createUser(request);
        authFacade.authenticateAndSave(request.username(), request.password(),
                httpRequest, httpResponse);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/sign-in")
    @Override
    public ResponseEntity<SignInResponse> login(@Valid @RequestBody SignInRequest request,
                                                HttpServletRequest httpRequest,
                                                HttpServletResponse httpResponse) {
        UserPrincipal principal = authFacade.authenticateAndSave(
                request.username(), request.password(), httpRequest, httpResponse);
        return ResponseEntity.ok(userMapper.toSignInResponse(principal));
    }
}
