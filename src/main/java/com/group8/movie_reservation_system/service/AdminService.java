package com.group8.movie_reservation_system.service;

import com.group8.movie_reservation_system.dto.request.RequestAdminDto;
import com.group8.movie_reservation_system.dto.response.ResponseAdminDto;

import java.util.List;

public interface AdminService {

    ResponseAdminDto createAdmin(RequestAdminDto dto, String performedByAdminId);
    List<ResponseAdminDto> getAllAdmins();
    ResponseAdminDto updateAdmin(String id, RequestAdminDto dto, String performedByAdminId);
    void deleteAdmin(String id, String performedByAdminId);
    ResponseAdminDto getAdminById(String id);
}
