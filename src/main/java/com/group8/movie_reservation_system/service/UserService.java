package com.group8.movie_reservation_system.service;

import com.group8.movie_reservation_system.dto.request.RequestLoginDto;
import com.group8.movie_reservation_system.dto.request.RequestUserDto;
import com.group8.movie_reservation_system.dto.response.ResponseUserDto;
import com.group8.movie_reservation_system.dto.response.paginate.UserPaginateResponseDto;

public interface UserService {
    void signup(RequestUserDto dto);
    void login(RequestLoginDto dto);
    ResponseUserDto findByUsername(String username);
    UserPaginateResponseDto getAllUsers(int page, int size);
    ResponseUserDto toggleUserStatus(String targetUserId,String loggedUserId);
    public long getUserCount();
    public ResponseUserDto updateUser(String id, RequestUserDto updatedUser);
    public void deleteUser(String id);
    long getTotalUserCount();
    UserPaginateResponseDto searchUsers(String searchTerm, org.springframework.data.domain.Pageable pageable);
    void updateUserRole(String userId, String newRole);

}