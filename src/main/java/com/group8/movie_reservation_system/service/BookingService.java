package com.group8.movie_reservation_system.service;


import com.group8.movie_reservation_system.dto.request.RequestBookingDto;
import com.group8.movie_reservation_system.dto.response.ResponseBookingDto;
import com.group8.movie_reservation_system.dto.response.paginate.AvailablePaginateResponseDto;
import com.group8.movie_reservation_system.dto.response.paginate.BookingPaginateResponseDto;



public interface BookingService {

    ResponseBookingDto createBooking(RequestBookingDto dto);

    void cancelBooking(Long bookingId);

    BookingPaginateResponseDto listBookingsByUser(int page, int size, String userId);

    AvailablePaginateResponseDto getAvailableSeats(int page, int suze , Long showtimeId);

    ResponseBookingDto updateBooking(RequestBookingDto dto ,Long bookingId);
}
