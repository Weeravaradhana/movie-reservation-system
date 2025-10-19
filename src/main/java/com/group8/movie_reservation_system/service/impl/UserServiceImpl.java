package com.group8.movie_reservation_system.service.impl;

import com.group8.movie_reservation_system.dto.request.RequestLoginDto;
import com.group8.movie_reservation_system.dto.request.RequestUserDto;
import com.group8.movie_reservation_system.dto.response.ResponseUserDto;
import com.group8.movie_reservation_system.dto.response.paginate.UserPaginateResponseDto;
import com.group8.movie_reservation_system.entity.User;
import com.group8.movie_reservation_system.entity.UserRole;
import com.group8.movie_reservation_system.entity.UserRoleHasUser;
import com.group8.movie_reservation_system.exception.BadCredentialsException;
import com.group8.movie_reservation_system.exception.BadRequestException;
import com.group8.movie_reservation_system.exception.DuplicateEntryException;
import com.group8.movie_reservation_system.exception.EntryNotFoundException;
import com.group8.movie_reservation_system.repo.UserRepository;
import com.group8.movie_reservation_system.repo.UserRoleRepo;
import com.group8.movie_reservation_system.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepo userRoleRepo;
    private final SystemLogService logService;


    @Override
    public void signup(RequestUserDto dto) {
        if(dto.getUsername().equalsIgnoreCase("admin@gmail.com")){
            String encodedPassword = passwordEncoder.encode(dto.getPassword());
            User user = User.builder()
                    .id(UUID.randomUUID().toString())
                    .username(dto.getUsername())
                    .passwordHash(encodedPassword)
                    .accountNonExpired(true)
                    .credentialsNonExpired(true)
                    .accountNonLocked(true)
                    .enabled(true)
                    .build();

            UserRole role = userRoleRepo.findByRoleName("ROLE_ADMIN")
                    .orElseThrow(()->new EntryNotFoundException("Role not found"));

            UserRoleHasUser userRoleHasUser = new UserRoleHasUser(user, role);
            if(user.getUserRoleHasUsers() == null) {
                user.setUserRoleHasUsers(new java.util.HashSet<>());
            }
            user.getUserRoleHasUsers().add(userRoleHasUser);
            if(role.getUserRoleHasUsers() == null) {
                role.setUserRoleHasUsers(new java.util.HashSet<>());
            }
            role.getUserRoleHasUsers().add(userRoleHasUser);
            userRepo.save(user);

        }else {

            String encodedPassword = passwordEncoder.encode(dto.getPassword());
            User user = User.builder()
                    .id(UUID.randomUUID().toString())
                    .username(dto.getUsername())
                    .passwordHash(encodedPassword)
                    .accountNonExpired(true)
                    .credentialsNonExpired(true)
                    .accountNonLocked(true)
                    .enabled(true)
                    .build();

            UserRole role = userRoleRepo.findByRoleName("ROLE_USER")
                    .orElseThrow(() -> new EntryNotFoundException("Role not found"));

            UserRoleHasUser userRoleHasUser = new UserRoleHasUser(user, role);
            if (user.getUserRoleHasUsers() == null) {
                user.setUserRoleHasUsers(new java.util.HashSet<>());
            }
            user.getUserRoleHasUsers().add(userRoleHasUser);
            if (role.getUserRoleHasUsers() == null) {
                role.setUserRoleHasUsers(new java.util.HashSet<>());
            }
            role.getUserRoleHasUsers().add(userRoleHasUser);
            userRepo.save(user);

        }

    }

    @Override
    public void login(RequestLoginDto dto) {
        String encode = passwordEncoder.encode("1234");
        System.out.println(encode);
        User user = userRepo.findByUsername(dto.getUsername())
                .orElseThrow(() -> new EntryNotFoundException("User not found"));



        if(!user.isEnabled()) {
            throw new BadRequestException("User is disabled");
        }

        if(!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

    }

    @Override
    public ResponseUserDto findByUsername(String username) {
       return toResponseUserDto(userRepo.findByUsername(username)
               .orElseThrow(()->new EntryNotFoundException("User not found")));
    }


    @Override

    public UserPaginateResponseDto getAllUsers(int page, int size) {
      return   UserPaginateResponseDto.builder()
                .dataCount(userRepo.countAllUsers())
                .dataList(
                        userRepo.searchAllUsers(PageRequest.of(page, size))
                                .stream().map(this::toResponseUserDto).collect(Collectors.toList())
                )
                .build();

    }

    @Override
    public ResponseUserDto toggleUserStatus(String id, String adminId) {
        return userRepo.findById(id).map(u -> {
            if ("ACTIVE".equalsIgnoreCase(u.getStatus())) {
                u.setStatus("SUSPENDED");
                logService.logAction(adminId, "Suspended user: " + u.getUsername());
            } else {
                u.setStatus("ACTIVE");
                logService.logAction(adminId, "Activated user: " + u.getUsername());
            }
            return toResponseUserDto(userRepo.save(u));
        }).orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    @Override
    public long getUserCount() {
        return userRepo.count();
    }

    @Override
    public ResponseUserDto updateUser(String id, RequestUserDto updatedUser) {
        return userRepo.findById(id)
                .map(user -> {
                    user.setFullName(updatedUser.getFullName());
                    user.setUsername(updatedUser.getUsername());

                    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                        user.setPasswordHash(passwordEncoder.encode(updatedUser.getPassword()));
                    }

                    User savedUser = userRepo.save(user);

                    logService.logAction(
                            savedUser.getId(),
                            "Updated user profile: " + savedUser.getUsername()
                    );

                   return toResponseUserDto(savedUser);
                })
                .orElseThrow(() -> new EntryNotFoundException("User not found with ID: " + id));
    }


    @Override
    public void deleteUser(String id) {
        User user = userRepo.findById(id).orElseThrow(() -> new EntryNotFoundException("User not found"));
        userRepo.deleteById(user.getId());
    }

    private ResponseUserDto toResponseUserDto(User user) {

        Set<String> roleNames = user.getUserRoleHasUsers().stream()
                .map(urhu -> urhu.getUserRole().getRoleName())
                .collect(Collectors.toSet());

        String role = roleNames.isEmpty() ? null : roleNames.iterator().next();

        return ResponseUserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(role)
                .build();
    }
}
