package metty1337.cloudfilestorage.controller.impl;

import io.swagger.v3.oas.annotations.Parameter;
import metty1337.cloudfilestorage.controller.UserControllerApi;
import metty1337.cloudfilestorage.dto.response.UserResponse;
import metty1337.cloudfilestorage.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController implements UserControllerApi {

    @GetMapping("/me")
    @Override
    public ResponseEntity<UserResponse> me(@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserResponse response = new UserResponse(userPrincipal.getUsername());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
