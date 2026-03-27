package metty1337.cloudfilestorage.security;

import lombok.RequiredArgsConstructor;
import metty1337.cloudfilestorage.entity.User;
import metty1337.cloudfilestorage.service.UserService;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    @NullMarked
    @Transactional(readOnly = true)
    public UserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPassword()
        );
    }
}