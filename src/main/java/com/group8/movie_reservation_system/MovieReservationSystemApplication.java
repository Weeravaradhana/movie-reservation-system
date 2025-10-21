package com.group8.movie_reservation_system;

import com.group8.movie_reservation_system.entity.UserRole;
import com.group8.movie_reservation_system.service.UserRoleService;
import com.group8.movie_reservation_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MovieReservationSystemApplication implements CommandLineRunner {
	@Autowired
 private UserRoleService userRoleService;
	public static void main(String[] args) {
		SpringApplication.run(MovieReservationSystemApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		userRoleService.initializeRoles();
	}
}
