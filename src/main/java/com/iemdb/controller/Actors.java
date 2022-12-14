package com.iemdb.controller;

import com.iemdb.Entity.Actor;
import com.iemdb.Entity.Movie;
import com.iemdb.Repository.ActorRepository;
import com.iemdb.Repository.MovieRepository;
import com.iemdb.model.IEMovieDataBase;
import com.iemdb.utils.Utils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@AllArgsConstructor
@RestController
public class Actors {

    private ActorRepository actorRepository;
    private MovieRepository movieRepository;

    @GetMapping("/actors")
    public List<Actor> getActors() {
        return StreamSupport.stream(actorRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @GetMapping("/actors/{id}")
    public ResponseEntity<Actor> getActor(@PathVariable int id) {
        Optional<Actor> actor = actorRepository.findById(id);
        return actor.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @GetMapping("/actors/{id}/movies")
    public ResponseEntity<List<Movie>> getActorMovies(@PathVariable int id) {
        
        try {
            Optional<Actor> optionalActor = actorRepository.findById(id);
            if (optionalActor.isEmpty())
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            Actor actor = optionalActor.get();
            List<Movie> movies = movieRepository.findAllByCast(actor.getId());
            return new ResponseEntity<>(movies, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
