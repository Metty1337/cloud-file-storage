package metty1337.cloudfilestorage.controller.impl;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import metty1337.cloudfilestorage.controller.UserControllerApi;
import metty1337.cloudfilestorage.dto.response.UserResponse;
import metty1337.cloudfilestorage.mapper.UserMapper;
import metty1337.cloudfilestorage.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController implements UserControllerApi {

    private final UserMapper userMapper;

    @GetMapping("/me")
    @Override
    public ResponseEntity<UserResponse> me(@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserResponse response = userMapper.toUserResponse(userPrincipal);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
