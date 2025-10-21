# Frontend Controllers Guide

## Overview

This document explains the clean separation between REST controllers and Frontend controllers in the Movie Reservation System. The frontend controllers are responsible only for serving Thymeleaf templates and calling REST APIs to get data.

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Frontend Layer                           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │ @Controller     │  │ @Controller     │  │ @Controller  │ │
│  │ FrontendAuth    │  │ FrontendMovie   │  │ FrontendAdmin│ │
│  │ - /login        │  │ - /movies       │  │ - /admin/*  │ │
│  │ - /signup       │  │ - /movie/{id}   │  │             │ │
│  │ - /logout       │  │ - /booking/*    │  │             │ │
│  └─────────────────┘  └─────────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ RestTemplate calls
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    REST API Layer                           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │ @RestController │  │ @RestController │  │ @RestController│ │
│  │ AuthController  │  │ MovieController │  │ AdminController│ │
│  │ - /api/auth/*   │  │ - /api/movies/* │  │ - /api/admin/*│ │
│  │ Returns JSON    │  │ Returns JSON    │  │ Returns JSON  │ │
│  └─────────────────┘  └─────────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## Frontend Controllers

### 1. FrontendAuthController
**Purpose**: Handle authentication UI routes
**Base URL**: `http://localhost:8001`

| Route | Method | Purpose | REST API Call |
|-------|--------|---------|---------------|
| `/login` | GET | Show login page | None |
| `/login` | POST | Handle login | `POST /user-management-service/api/v1/users/visitors/login` |
| `/signup` | GET | Show signup page | None |
| `/signup` | POST | Handle signup | `POST /user-management-service/api/v1/users/visitors/signup` |
| `/logout` | GET | Handle logout | `POST /user-management-service/api/v1/users/visitors/logout` |

**Example Code**:
```java
@PostMapping("/login")
public String handleLogin(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
    try {
        // Create login request DTO
        RequestLoginDto loginDto = new RequestLoginDto();
        loginDto.setUsername(username);
        loginDto.setPassword(password);

        // Set headers for REST API call
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RequestLoginDto> request = new HttpEntity<>(loginDto, headers);

        // Call REST API for login
        String url = BASE_URL + "/user-management-service/api/v1/users/visitors/login";
        ResponseEntity<StandardResponseDto> response = restTemplate.postForEntity(url, request, StandardResponseDto.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            // Handle successful login
            return "redirect:/customer/dashboard";
        }
        
        // Handle failed login
        model.addAttribute("error", "Invalid username or password");
        return "login";
        
    } catch (Exception e) {
        model.addAttribute("error", "Login failed. Please try again.");
        return "login";
    }
}
```

### 2. FrontendMovieController
**Purpose**: Handle movie-related UI routes
**Base URL**: `http://localhost:8001`

| Route | Method | Purpose | REST API Call |
|-------|--------|---------|---------------|
| `/movies` | GET | Show movies listing | `GET /movie-management-service/api/v1/movies/visitors/find-all` |
| `/movie/{id}` | GET | Show movie details | `GET /movie-management-service/api/v1/movies/visitor/{id}` |
| `/booking/movie/{id}` | GET | Show booking page | `GET /movie-management-service/api/v1/movies/visitor/{id}` |

**Example Code**:
```java
@GetMapping("/movies")
public String showMovies(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "12") int size,
                        @RequestParam(required = false) String genre,
                        @RequestParam(required = false) String search,
                        Model model,
                        HttpSession session) {
    try {
        // Build URL with query parameters
        StringBuilder urlBuilder = new StringBuilder(BASE_URL + "/movie-management-service/api/v1/movies/visitors/find-all");
        urlBuilder.append("?page=").append(page).append("&size=").append(size);
        
        if (genre != null && !genre.isEmpty()) {
            urlBuilder.append("&genre=").append(genre);
        }
        if (search != null && !search.isEmpty()) {
            urlBuilder.append("&search=").append(search);
        }
        
        // Call REST API to get movies
        ResponseEntity<MoviePaginateResponseDto> response = restTemplate.getForEntity(urlBuilder.toString(), MoviePaginateResponseDto.class);
        
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            MoviePaginateResponseDto movieResponse = response.getBody();
            List<ResponseMovieDto> movies = movieResponse.getDataList();
            
            // Add data to model for Thymeleaf template
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
```

### 3. FrontendCustomerController
**Purpose**: Handle customer dashboard and profile management
**Base URL**: `http://localhost:8001`

| Route | Method | Purpose | REST API Call |
|-------|--------|---------|---------------|
| `/customer/dashboard` | GET | Show customer dashboard | `GET /user-management-service/api/v1/users/{id}`<br>`GET /booking-management-service/api/v1/bookings/user/{id}` |
| `/customer/profile/update` | POST | Update profile | `PUT /user-management-service/api/v1/users/{id}` |
| `/bookings/{id}/cancel` | POST | Cancel booking | `POST /booking-management-service/api/v1/bookings/cancel/{id}` |
| `/bookings/{id}` | GET | Show booking details | `GET /booking-management-service/api/v1/bookings/{id}` |

### 4. FrontendAdminController
**Purpose**: Handle admin dashboard and management operations
**Base URL**: `http://localhost:8001`

| Route | Method | Purpose | REST API Call |
|-------|--------|---------|---------------|
| `/admin/dashboard` | GET | Show admin dashboard | Multiple API calls for statistics |
| `/admin/movies/add` | POST | Add new movie | `POST /movie-management-service/api/v1/movies/admin/create` |
| `/admin/movies/{id}/delete` | POST | Delete movie | `DELETE /movie-management-service/api/v1/movies/admin/{id}` |
| `/admin/movies/{id}/edit` | GET | Show edit form | `GET /movie-management-service/api/v1/movies/visitor/{id}` |
| `/admin/movies/{id}/edit` | POST | Update movie | `PUT /movie-management-service/api/v1/movies/admin/{id}` |

### 5. FrontendHomeController
**Purpose**: Handle home page and general navigation
**Base URL**: `http://localhost:8001`

| Route | Method | Purpose | REST API Call |
|-------|--------|---------|---------------|
| `/` | GET | Show home page | `GET /movie-management-service/api/v1/movies/visitors/find-all` |
| `/home` | GET | Show home page | `GET /movie-management-service/api/v1/movies/visitors/find-all` |
| `/movies` | GET | Show movies listing | `GET /movie-management-service/api/v1/movies/visitors/find-all` |

## Key Principles

### 1. Separation of Concerns
- **Frontend Controllers (@Controller)**: Handle UI routes, call REST APIs, return Thymeleaf templates
- **REST Controllers (@RestController)**: Handle API routes, return JSON data, contain business logic

### 2. Data Flow
```
User Request → Frontend Controller → REST API Call → Model Attributes → Thymeleaf Template → HTML Response
```

### 3. Error Handling
```java
try {
    // Call REST API
    ResponseEntity<ResponseDto> response = restTemplate.getForEntity(url, ResponseDto.class);
    
    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
        // Success - add data to model
        model.addAttribute("data", response.getBody());
    } else {
        // API returned error
        model.addAttribute("error", "Unable to load data");
    }
} catch (Exception e) {
    // Network or other error
    model.addAttribute("error", "Service temporarily unavailable");
}
```

### 4. Session Management
```java
// Check authentication
String userId = (String) session.getAttribute("loggedUserId");
if (userId == null) {
    return "redirect:/login?error=Please login";
}

// Add session info to model
model.addAttribute("session", session);
```

### 5. Redirects
```java
// Success redirect
return "redirect:/customer/dashboard";

// Error redirect with message
return "redirect:/login?error=Invalid credentials";
```

## Configuration

### RestTemplate Configuration
```java
@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

### Application Properties
```properties
# Thymeleaf Configuration
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
spring.thymeleaf.cache=false

# Server Configuration
server.port=8001
```

## Template Structure

```
src/main/resources/
├── templates/
│   ├── fragments/
│   │   ├── header.html
│   │   └── footer.html
│   ├── admin/
│   │   └── dashboard.html
│   ├── customer/
│   │   └── dashboard.html
│   ├── booking/
│   │   ├── movie-booking.html
│   │   ├── seats.html
│   │   └── payment.html
│   ├── index.html
│   ├── login.html
│   ├── signup.html
│   ├── movies.html
│   ├── movie-details.html
│   └── error.html
└── static/
    ├── css/
    │   └── style.css
    └── js/
        └── app.js
```

## Usage Examples

### 1. Getting Movies for Home Page
```java
@GetMapping("/")
public String showHomePage(Model model, HttpSession session) {
    try {
        String url = BASE_URL + "/movie-management-service/api/v1/movies/visitors/find-all?page=0&size=6";
        ResponseEntity<MoviePaginateResponseDto> response = restTemplate.getForEntity(url, MoviePaginateResponseDto.class);
        
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            model.addAttribute("movies", response.getBody().getDataList());
        } else {
            model.addAttribute("movies", List.of());
        }
    } catch (Exception e) {
        model.addAttribute("movies", List.of());
        model.addAttribute("error", "Unable to load movies");
    }
    
    model.addAttribute("session", session);
    return "index";
}
```

### 2. Creating a New Movie (Admin)
```java
@PostMapping("/admin/movies/add")
public String addMovie(@RequestParam String title,
                      @RequestParam String genre,
                      @RequestParam Integer duration,
                      @RequestParam Double rating,
                      @RequestParam Double price,
                      @RequestParam String posterUrl,
                      @RequestParam String description,
                      HttpSession session,
                      Model model) {
    // Check admin access
    String userRole = (String) session.getAttribute("loggedUserRole");
    if (!"ROLE_ADMIN".equals(userRole)) {
        return "redirect:/login?error=Admin access required";
    }

    try {
        // Create request DTO
        RequestMovieDto movieDto = new RequestMovieDto();
        movieDto.setTitle(title);
        movieDto.setGenre(genre);
        movieDto.setDuration(duration);
        movieDto.setRating(rating);
        movieDto.setPrice(price);
        movieDto.setPosterUrl(posterUrl);
        movieDto.setDescription(description);

        // Call REST API
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RequestMovieDto> request = new HttpEntity<>(movieDto, headers);
        
        String url = BASE_URL + "/movie-management-service/api/v1/movies/admin/create";
        ResponseEntity<StandardResponseDto> response = restTemplate.postForEntity(url, request, StandardResponseDto.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            if (response.getBody().getCode() == 201) {
                model.addAttribute("success", "Movie added successfully");
            } else {
                model.addAttribute("error", "Failed to add movie: " + response.getBody().getMessage());
            }
        } else {
            model.addAttribute("error", "Failed to add movie");
        }
    } catch (Exception e) {
        model.addAttribute("error", "Failed to add movie. Please try again.");
    }

    return "redirect:/admin/dashboard";
}
```

## Benefits of This Architecture

1. **Clean Separation**: REST controllers handle business logic, frontend controllers handle UI
2. **Reusability**: REST APIs can be used by mobile apps, other frontends, etc.
3. **Maintainability**: Changes to business logic don't affect UI controllers
4. **Testability**: Each layer can be tested independently
5. **Scalability**: Frontend and backend can be deployed separately if needed

## Best Practices

1. **Always use try-catch** when calling REST APIs
2. **Handle errors gracefully** and provide user-friendly messages
3. **Check authentication** before accessing protected resources
4. **Use appropriate HTTP methods** (GET for data retrieval, POST for actions)
5. **Validate input** before sending to REST APIs
6. **Use meaningful redirects** with appropriate messages
7. **Keep controllers focused** on single responsibilities
8. **Use consistent naming** for routes and methods
