package net.wuxianjie.web.service;

import net.wuxianjie.core.dto.PaginationDto;
import net.wuxianjie.core.dto.PaginationQueryDto;
import net.wuxianjie.web.dto.UserDto;
import net.wuxianjie.web.dto.Wrote2DbDto;

import java.util.List;

public interface UserService {

    UserDto getUser(String username);

    PaginationDto<List<UserDto>> getUsers(PaginationQueryDto pagination, String fuzzyUsername);

    Wrote2DbDto saveUser(UserDto userToAdd);

    Wrote2DbDto updateUser(UserDto userToUpdate);

    Wrote2DbDto updatePassword(UserDto passwordToUpdate);

    Wrote2DbDto removeUser(int userId);
}
