package com.backend.gapfinder.entities.rating;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;
    private final ModelMapper modelMapper;

    public RatingController(RatingService ratingService, ModelMapper modelMapper) {
        this.ratingService = ratingService;
        this.modelMapper = modelMapper;
    }

    // Crea una calificación a partir de un match o una Open Table
    // POST /ratings?raterId=1&matchId=2&rating=5&wouldRepeat=true
    // o POST /ratings?raterId=1&openTableId=3&rating=4&wouldRepeat=false
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RatingBasicDTO createRating(
            @RequestParam Long raterId,
            @RequestParam(required = false) Long matchId,
            @RequestParam(required = false) Long openTableId,
            @RequestParam(required = false) Long ratedUserId,
            @RequestParam int rating,
            @RequestParam(required = false) Boolean wouldRepeat) {

        RatingEntity created = ratingService.create(raterId, matchId, openTableId, ratedUserId, rating, wouldRepeat);
        return modelMapper.map(created, RatingBasicDTO.class);
    }

    // Obtiene todas las calificaciones recibidas por un usuario
    // GET /ratings/user/{ratedUserId}
    @GetMapping("/user/{ratedUserId}")
    public List<RatingBasicDTO> getRatingsByUser(@PathVariable Long ratedUserId) {
        List<RatingEntity> ratings = ratingService.getAllByRatedUser(ratedUserId);
        return modelMapper.map(ratings, new TypeToken<List<RatingBasicDTO>>() {}.getType());
    }

    // Obtiene una calificación por id
    // GET /ratings/{id}
    @GetMapping("/{id}")
    public RatingCompleteDTO getRating(@PathVariable Long id) {
        RatingEntity rating = ratingService.getById(id);
        return modelMapper.map(rating, RatingCompleteDTO.class);
    }
}
