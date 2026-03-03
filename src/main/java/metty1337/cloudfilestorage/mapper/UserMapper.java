package metty1337.cloudfilestorage.mapper;

import metty1337.cloudfilestorage.dto.request.SignUpRequest;
import metty1337.cloudfilestorage.dto.response.SignUpResponse;
import metty1337.cloudfilestorage.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "withPassword", ignore = true)
    User toEntity(SignUpRequest signUpRequest);

    SignUpResponse toDto(User user);
}