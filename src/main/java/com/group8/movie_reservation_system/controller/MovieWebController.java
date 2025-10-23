package com.group8.movie_reservation_system.controller;

import com.group8.movie_reservation_system.dto.response.ResponseMovieDto;
import com.group8.movie_reservation_system.dto.response.ResponseShowtimeDto;
import com.group8.movie_reservation_system.exception.EntryNotFoundException;
import com.group8.movie_reservation_system.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MovieWebController {

    @Autowired
    private RestTemplate restTemplate;
    private final ShowtimeService showtimeService;

    private static final String BASE_URL = "http://localhost:8001";

    @GetMapping("/movie/{id}")
    public String movieDetails(@PathVariable Long id, Model model, HttpSession session) {
        try {
            String movieUrl = BASE_URL + "/movie-management-service/api/v1/movies/visitor/" + id;
            ResponseEntity<ResponseMovieDto> movieResponse = restTemplate.getForEntity(movieUrl, ResponseMovieDto.class);

            model.addAttribute("movieId", id);
            if (movieResponse.getStatusCode().is2xxSuccessful() && movieResponse.getBody() != null) {
                ResponseMovieDto movie = movieResponse.getBody();
                model.addAttribute("movie", movie);
                

                try {
                    String showtimeUrl = BASE_URL + "/showtime-management-service/api/v1/showtimes/movie/" + id;
                    ResponseEntity<List> showtimeResponse = restTemplate.getForEntity(showtimeUrl, List.class);
                    
                    if (showtimeResponse.getStatusCode().is2xxSuccessful() && showtimeResponse.getBody() != null) {
                        model.addAttribute("showtimes", showtimeResponse.getBody());
                    }
                } catch (Exception e) {
                    model.addAttribute("showtimes", List.of());
                }
                

                try {
                    String reviewUrl = BASE_URL + "/review-management-service/api/v1/reviews/movie/" + id;
                    ResponseEntity<List> reviewResponse = restTemplate.getForEntity(reviewUrl, List.class);
                    
                    if (reviewResponse.getStatusCode().is2xxSuccessful() && reviewResponse.getBody() != null) {
                        model.addAttribute("reviews", reviewResponse.getBody());
                    }
                } catch (Exception e) {
                    model.addAttribute("reviews", List.of());
                }
                
            } else {
                model.addAttribute("error", "Movie not found");
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Unable to load movie details");
            return "error";
        }

        model.addAttribute("session", session);
        return "movie-details";
    }

    @GetMapping("/booking/movie/{id}")
    public String bookingPage(@PathVariable Long id, Model model, HttpSession session) {
        ResponseShowtimeDto responseShowtimeDto = showtimeService.getShowTimesByMovieId(id).
                stream().findFirst()
                .orElseThrow(() -> new EntryNotFoundException("Entry not found"));
        if (session.getAttribute("loggedUserId") == null) {
            return "redirect:/login?error=Please login to book tickets";
        }

        try {
            String movieUrl = BASE_URL + "/movie-management-service/api/v1/movies/visitor/" + id;
            ResponseEntity<ResponseMovieDto> movieResponse = restTemplate.getForEntity(movieUrl, ResponseMovieDto.class);
            ResponseEntity<ResponseShowtimeDto> showtimeResponse = restTemplate.getForEntity(movieUrl, ResponseShowtimeDto.class);

           if (movieResponse.getStatusCode().is2xxSuccessful() && movieResponse.getBody() != null) {
                ResponseMovieDto movie = movieResponse.getBody();
                ResponseShowtimeDto showtime = showtimeResponse.getBody();
                model.addAttribute("movie", movie);
                model.addAttribute("showtime", showtime);

                try {
                    String showtimeUrl = BASE_URL + "/showtime-management-service/api/v1/showtimes/movie/" + id + "/available";
                  //  ResponseEntity<List> showtimeResponse = restTemplate.getForEntity(showtimeUrl, List.class);
                    
                    if (showtimeResponse.getStatusCode().is2xxSuccessful() && showtimeResponse.getBody() != null) {
                        model.addAttribute("showtimes", showtimeResponse.getBody());
                    } else {
                        model.addAttribute("showtimes", List.of());
                    }
                } catch (Exception e) {
                    model.addAttribute("showtimes", List.of());
                }
                
            }
        else {
                model.addAttribute("error", "Movie not found");
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Unable to load booking information");
            return "error";
        }

        model.addAttribute("session", session);
        return "booking/movie-booking";
    }

    @GetMapping("/booking/showtime/{showtimeId}")
    public String seatSelection(@PathVariable String showtimeId, Model model, HttpSession session) {

        // Check if user is logged in
        if (session.getAttribute("loggedUserId") == null) {
            return "redirect:/login?error=Please login to book tickets";
        }

        Long showtimeIdLong;
        try {
            showtimeIdLong = Long.parseLong(showtimeId);
        } catch (NumberFormatException e) {
            model.addAttribute("error", "Invalid showtime ID");
            return "error";
        }

        try {
            // Fetch showtime details
            String showtimeUrl = BASE_URL + "/showtime-management-service/api/v1/showtimes/" + showtimeIdLong;
            ResponseEntity<ResponseShowtimeDto> showtimeResponse =
                    restTemplate.getForEntity(showtimeUrl, ResponseShowtimeDto.class);

            if (showtimeResponse.getStatusCode().is2xxSuccessful() && showtimeResponse.getBody() != null) {
                ResponseShowtimeDto showtime = showtimeResponse.getBody();
                model.addAttribute("showtime", showtime);

                // Fetch movie details
                String movieUrl = BASE_URL + "/movie-management-service/api/v1/movies/visitor/" + showtime.getMovie().getId();
                ResponseEntity<ResponseMovieDto> movieResponse =
                        restTemplate.getForEntity(movieUrl, ResponseMovieDto.class);

                if (movieResponse.getStatusCode().is2xxSuccessful() && movieResponse.getBody() != null) {
                    model.addAttribute("movie", movieResponse.getBody());
                }

                try {
                    String hallUrl = BASE_URL + "/hall-management-service/api/v1/halls/" + showtime.getHall().getHallId();
                    ResponseEntity<Object> hallResponse = restTemplate.getForEntity(hallUrl, Object.class);
                    if (hallResponse.getStatusCode().is2xxSuccessful() && hallResponse.getBody() != null) {
                        model.addAttribute("hall", hallResponse.getBody());
                    }
                } catch (Exception e) {
                }

                try {
                    String seatUrl = BASE_URL + "/seat-management-service/api/v1/seats/hall/" + showtime.getHall().getHallId();
                    ResponseEntity<List> seatResponse = restTemplate.getForEntity(seatUrl, List.class);

                    if (seatResponse.getStatusCode().is2xxSuccessful() && seatResponse.getBody() != null) {
                        model.addAttribute("seatRows", seatResponse.getBody());
                    } else {
                        model.addAttribute("seatRows", List.of());
                    }
                } catch (Exception e) {
                    model.addAttribute("seatRows", List.of());
                }

            } else {
                model.addAttribute("error", "Showtime not found");
                return "error";
            }

        } catch (Exception e) {
            model.addAttribute("error", "Unable to load seat selection");
            return "error";
        }

        model.addAttribute("session", session);
        return "booking/seats";
    }
}
