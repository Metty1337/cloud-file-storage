package metty1337.cloudfilestorage.mapper;

import metty1337.cloudfilestorage.dto.response.SignInResponse;
import metty1337.cloudfilestorage.dto.response.SignUpResponse;
import metty1337.cloudfilestorage.dto.response.UserResponse;
import metty1337.cloudfilestorage.entity.User;
import metty1337.cloudfilestorage.security.UserPrincipal;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    SignUpResponse toSignUpResponse(User user);

    SignInResponse toSignInResponse(UserPrincipal userPrincipal);

    UserResponse toUserResponse(UserPrincipal userPrincipal);
}