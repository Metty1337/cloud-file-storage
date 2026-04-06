package metty1337.cloudfilestorage.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metty1337.cloudfilestorage.security.UserPrincipal;

public interface AuthFacade {
    UserPrincipal authenticateAndSave(String username, String password,
                                      HttpServletRequest request,
                                      HttpServletResponse response);
}
