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
public class ResponseDealDto {
    private String dealId;
    private int code;
    private Date startDate;
    private Date endDate;
    private double discount;
    private boolean status;
    private String bookingId;
    private String adminId;

}
