package com.musicfever.music_fever.dto.user;

import com.musicfever.music_fever.model.UserEntity;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    ActiveUserDTO toPrincipalDTO(UserEntity user);
    List<ActiveUserDTO> toPrincipalDTOs(Collection<UserEntity> users);

    @Mapping(target = "email", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "profilePicture", ignore = true)
    @Mapping(target = "roles", ignore = true)
    UserEntity toDomain(ActiveUserDTO userDTO);
}
