package com.group8.movie_reservation_system.service;

import com.group8.movie_reservation_system.dto.request.RequestShowtimeDto;
import com.group8.movie_reservation_system.dto.response.ResponseShowtimeDto;

import java.util.List;

public interface ShowtimeService{

    ResponseShowtimeDto createShowtime(RequestShowtimeDto dto, String adminId);
    List<ResponseShowtimeDto> getAllShowTimes();
    ResponseShowtimeDto updateShowtime(Long id, RequestShowtimeDto dto, String adminId);
    void deleteShowtime(Long id, String adminId);
    long getShowtimeCount();
    public ResponseShowtimeDto getShowtimeById(Long id);
    public List<ResponseShowtimeDto> getShowTimesByMovieId(Long movieId);
}