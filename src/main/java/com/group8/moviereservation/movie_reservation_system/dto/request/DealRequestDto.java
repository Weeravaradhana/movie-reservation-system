package com.group8.moviereservation.movie_reservation_system.dto.request;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DealRequestDto {
    private int code;
    private Date startDate;
    private Date endDate;
    private double discount;
    private String description;
    private boolean status;

}
