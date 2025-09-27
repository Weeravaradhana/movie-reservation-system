package com.group8.moviereservation.movie_reservation_system.service.impl;

import com.group8.moviereservation.movie_reservation_system.dto.request.UserRequestDto;
import com.group8.moviereservation.movie_reservation_system.entity.User;
import com.group8.moviereservation.movie_reservation_system.repo.UserRepo;
import com.group8.moviereservation.movie_reservation_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo customerRepo;

    @Override
    public void signup(UserRequestDto dto) {
        try {
            customerRepo.save(toUser(dto));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Object userLogin(UserRequestDto dto) {
        /*Optional<Customer> selectedCustomer = customerRepo.findByEmail(dto.getEmail());

        if(selectedCustomer.isEmpty()){
            throw new EntryNotFoundException("can't find the associated customer");
        }

        Customer customer = selectedCustomer.get();
        if(!customer.getEmail().equals(dto.getEmail())){
            throw new UnAuthorizedException("please verify email");

        }*/

        return null;
    }

    private User toUser(UserRequestDto dto) throws SQLException, ClassNotFoundException {
        return dto == null? null :
                User.builder()
                        .Username(dto.getUsername())
                        .customerId(UUID.randomUUID().toString())
                        .password(dto.getPassword())
                        .email(dto.getEmail())
                        .build();
    }
}
