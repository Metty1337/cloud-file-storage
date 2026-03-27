package metty1337.cloudfilestorage.controller;

import metty1337.cloudfilestorage.dto.response.UserResponse;
import metty1337.cloudfilestorage.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserResponse response = new UserResponse(userPrincipal.getUsername());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
