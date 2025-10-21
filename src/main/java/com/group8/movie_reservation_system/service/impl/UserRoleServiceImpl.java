package com.group8.movie_reservation_system.service.impl;


import com.group8.movie_reservation_system.entity.UserRole;
import com.group8.movie_reservation_system.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private com.group8.movie_reservation_system.repo.UserRoleRepo userRoleRepo;


    public void initializeRoles() {
        if(userRoleRepo.count() == 0) {
            UserRole admin = new UserRole("ROLE_ADMIN","ROLE_USER");
            UserRole user = new UserRole("ROLE_USER","ROLE_USER");

            userRoleRepo.saveAll(List.of(admin, user));
        }
    }
}