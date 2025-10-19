package com.group8.movie_reservation_system.service.impl;

import com.group8.movie_reservation_system.dto.request.RequestAdminDto;
import com.group8.movie_reservation_system.dto.response.ResponseAdminDto;
import com.group8.movie_reservation_system.entity.Admin;
import com.group8.movie_reservation_system.entity.User;
import com.group8.movie_reservation_system.exception.BadRequestException;
import com.group8.movie_reservation_system.exception.DuplicateEntryException;
import com.group8.movie_reservation_system.exception.EntryNotFoundException;
import com.group8.movie_reservation_system.repo.AdminRepository;
import com.group8.movie_reservation_system.repo.UserRepository;
import com.group8.movie_reservation_system.service.AdminService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SystemLogService logService;

    public ResponseAdminDto createAdmin(RequestAdminDto dto, String performedByAdminId) {

        if (dto.getFullName() == null || dto.getFullName().trim().isEmpty()) {
            throw new BadRequestException("Full name is required");
        }

        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new BadRequestException("Password is required");
        }

        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }



                 userRepository.findByUsername(dto.getUsername())
                .map(u->adminRepository
                        .findById(u.getId()))
                .orElseThrow(()-> new DuplicateEntryException("User already exists"));



        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .username(dto.getUsername())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .status(dto.getStatus())
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .enabled(true)
                .build();

        userRepository.save(user);

        String hashedPassword = user.getPasswordHash();
        Admin admin = toEntity(dto, hashedPassword, user);

        Admin saved = adminRepository.save(admin);

        logService.logAction(performedByAdminId, "Created admin: " + saved.getFullName());

        return mapToResponseDto(saved);
    }

    public List<ResponseAdminDto> getAllAdmins() {
        return adminRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public ResponseAdminDto updateAdmin(String id, RequestAdminDto dto, String performedByAdminId) {
        return adminRepository.findById(id)
                .map(admin -> {
                    admin.setFullName(dto.getFullName());
                    admin.setUserName(dto.getUsername());
                    admin.setRole(dto.getRole());
                    admin.setStatus(dto.getStatus());

                    if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                        String hashed = passwordEncoder.encode(dto.getPassword());
                        admin.setPasswordHash(hashed);

                        if (admin.getUser() != null) {
                            admin.getUser().setPasswordHash(hashed);
                            userRepository.save(admin.getUser());
                        }
                    }

                    Admin updated = adminRepository.save(admin);
                    logService.logAction(performedByAdminId, "Updated admin: " + updated.getUserName());
                    return mapToResponseDto(updated);
                })
                .orElseThrow(() -> new EntryNotFoundException("Admin not found with ID: " + id));
    }

    public void deleteAdmin(String id, String performedByAdminId) {
        adminRepository.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("Admin not found with ID: " + id));
       adminRepository.deleteById(id);
        logService.logAction(performedByAdminId, "Deleted admin with ID: " + id);

    }

    public ResponseAdminDto getAdminById(String id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("Admin not found with ID: " + id));
        return mapToResponseDto(admin);
    }

    private ResponseAdminDto mapToResponseDto(Admin admin) {
        return ResponseAdminDto.builder()
                .adminId(admin.getAdminId())
                .fullName(admin.getFullName())
                .username(admin.getUserName())
                .role(admin.getRole())
                .status(admin.getStatus())
                .userId(admin.getUser() != null ? admin.getUser().getId() : null)
                .build();
    }

    private Admin toEntity(RequestAdminDto dto, String hashedPassword, User user) {
        return Admin.builder()
                .adminId(user.getId())
                .fullName(dto.getFullName())
                .passwordHash(hashedPassword)
                .userName(dto.getUsername())
                .role(dto.getRole())
                .status(dto.getStatus())
                .user(user)
                .build();
    }
}
