package com.group8.movie_reservation_system.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestPromoCodeDto {
    private int code;
    private Date startDate;
    private Date endDate;
    private boolean status;
    private double discount;
    private String description;
}
