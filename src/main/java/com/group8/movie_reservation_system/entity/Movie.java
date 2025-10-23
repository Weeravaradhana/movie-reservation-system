package com.group8.movie_reservation_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Movies")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long id;

    private String title;

    @Column(columnDefinition = "nvarchar(max)")
    private String description;

    private Integer duration;
    private String genre;
    private double rating;

    @Column(columnDefinition = "VARCHAR(MAX)")
    private String trailer_url;

    private String status = "ACTIVE";

    // 🔹 Avoid recursion when serializing
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Showtime> showtimes = new ArrayList<>();

    // 🔹 Added to avoid infinite recursion with Review
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Review> reviews = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;
}
