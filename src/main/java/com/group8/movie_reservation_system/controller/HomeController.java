package com.group8.movie_reservation_system.controller;

import com.group8.movie_reservation_system.dto.response.ResponseMovieDto;
import com.group8.movie_reservation_system.dto.response.paginate.MoviePaginateResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String BASE_URL = "http://localhost:8001";

    @GetMapping("/home")
    public String home(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model,
                       HttpSession session) {
        System.out.println("helllo pro");
        try {
            // Fetch movies from REST API
            String url = BASE_URL + "/movie-management-service/api/v1/movies/visitors/find-all?page=" + page + "&size=" + size;
            
            ResponseEntity<MoviePaginateResponseDto> response = restTemplate.getForEntity(url, MoviePaginateResponseDto.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                MoviePaginateResponseDto movieResponse = response.getBody();
                List<ResponseMovieDto> movies = movieResponse.getDataList();
                
                model.addAttribute("movies", movies);
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", movieResponse.getDataCount() / size);
                model.addAttribute("totalMovies", movieResponse.getDataCount());
            } else {
                model.addAttribute("movies", List.of());
                model.addAttribute("message", "No movies available at the moment.");
            }
        } catch (Exception e) {
            model.addAttribute("movies", List.of());
            model.addAttribute("error", "Unable to load movies. Please try again later.");
        }

        // Add session info for header
        model.addAttribute("session", session);
        return "index";
    }

    @GetMapping("/movies")
    public String movies(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "12") int size,
                        @RequestParam(required = false) String genre,
                        @RequestParam(required = false) String search,
                        Model model,
                        HttpSession session) {
        try {
            // Build URL with filters
            StringBuilder urlBuilder = new StringBuilder(BASE_URL + "/movie-management-service/api/v1/movies/visitors/find-all");
            urlBuilder.append("?page=").append(page).append("&size=").append(size);
            
            if (genre != null && !genre.isEmpty()) {
                urlBuilder.append("&genre=").append(genre);
            }
            if (search != null && !search.isEmpty()) {
                urlBuilder.append("&search=").append(search);
            }
            
            ResponseEntity<MoviePaginateResponseDto> response = restTemplate.getForEntity(urlBuilder.toString(), MoviePaginateResponseDto.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                MoviePaginateResponseDto movieResponse = response.getBody();
                List<ResponseMovieDto> movies = movieResponse.getDataList();
                
                model.addAttribute("movies", movies);
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", (int) Math.ceil((double) movieResponse.getDataCount() / size));
                model.addAttribute("totalMovies", movieResponse.getDataCount());
                model.addAttribute("selectedGenre", genre);
                model.addAttribute("searchTerm", search);
            } else {
                model.addAttribute("movies", List.of());
                model.addAttribute("message", "No movies found matching your criteria.");
            }
        } catch (Exception e) {
            model.addAttribute("movies", List.of());
            model.addAttribute("error", "Unable to load movies. Please try again later.");
        }

        model.addAttribute("session", session);
        return "movies";
    }
}
