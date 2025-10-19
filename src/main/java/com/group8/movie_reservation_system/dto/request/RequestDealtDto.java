package com.group8.movie_reservation_system.dto.request;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestDealtDto {
    private int code;
    private Date startDate;
    private Date endDate;
    private double discount;
    private String description;
    private boolean status;
    private String promoCodeId;
}
