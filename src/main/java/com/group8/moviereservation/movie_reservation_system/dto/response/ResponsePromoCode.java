package com.group8.moviereservation.movie_reservation_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponsePromoCode {
    private String id;
    private int code;
    private Date startDate;
    private Date endDate;
    private boolean status;
    private double discount;
    private ResponseAdminDto admin;

}
