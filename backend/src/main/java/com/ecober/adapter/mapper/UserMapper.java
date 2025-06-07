package com.ecober.adapter.mapper;

import org.mapstruct.Mapper;

import com.ecober.adapter.Dto.UserDTO;
import com.ecober.domain.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDto(User user);
    User toEntity(UserDTO userDTO);
    
}
