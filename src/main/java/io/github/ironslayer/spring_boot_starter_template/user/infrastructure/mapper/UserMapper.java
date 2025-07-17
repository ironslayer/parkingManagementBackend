package io.github.ironslayer.spring_boot_starter_template.user.infrastructure.mapper;


import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.RegisterUserDTO;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.api.dto.UserDTO;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.entity.UserCacheEntity;
import io.github.ironslayer.spring_boot_starter_template.user.infrastructure.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.WARN)
public interface UserMapper {

    User userEntityToUser(UserEntity userEntity);

    UserEntity userToUserEntity(User user);

    UserDTO userToUserDTO(User user);

    User userDTOToUser(UserDTO userDTO);

    User registerUserDTOToUser(RegisterUserDTO registerUserDTO);

    UserCacheEntity userToUserCacheEntity(User product);

    User userCacheEntityToUser(UserCacheEntity product);
}
