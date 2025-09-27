package com.group8.moviereservation.movie_reservation_system.service;
import com.group8.moviereservation.movie_reservation_system.dto.request.UserRequestDto;

public interface UserService {
    public void signup(UserRequestDto dto);
    public Object userLogin(UserRequestDto dto);
}
