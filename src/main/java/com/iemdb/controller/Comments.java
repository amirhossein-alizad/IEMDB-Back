package com.iemdb.controller;

import com.iemdb.Domain.CommentVote;
import com.iemdb.Entity.Comment;
import com.iemdb.Entity.Movie;
import com.iemdb.Entity.User;
import com.iemdb.Repository.CommentRepository;
import com.iemdb.Repository.UserRepository;
import com.iemdb.exception.CommentNotFound;
import com.iemdb.exception.LoginRequired;
import com.iemdb.exception.MovieNotFound;
import com.iemdb.exception.RestException;
import com.iemdb.model.CurrentUser;
import com.iemdb.model.IEMovieDataBase;
import com.iemdb.utils.Utils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class Comments {
    private UserRepository userRepository;
    private CommentRepository commentRepository;

    @PostMapping("/comments/{id}/dislike")
    public ResponseEntity<String> dislikeComment(@PathVariable int id) {
        
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            User user = (User) request.getAttribute("user");
            Optional<Comment> comment = commentRepository.findById(id);
            if(comment.isEmpty())
                throw new CommentNotFound();
            Comment comment1 = comment.get();
            comment1.addVote(new CommentVote(user.getEmail(), id, -1));
            commentRepository.save(comment1);
            return new ResponseEntity<>("Comment voted successfully!", HttpStatus.OK);
        } catch (RestException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/comments/{id}/like")
    public ResponseEntity<String> likeComment(@PathVariable int id) {
        
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            User user = (User) request.getAttribute("user");
            Optional<Comment> comment = commentRepository.findById(id);
            if(comment.isEmpty())
                throw new CommentNotFound();
            Comment comment1 = comment.get();
            comment1.addVote(new CommentVote(user.getEmail(), id, 1));
            commentRepository.save(comment1);
            return new ResponseEntity<>("Comment voted successfully!", HttpStatus.OK);
        } catch (RestException e) {
            return new ResponseEntity<>(e.getMessage(),e.getStatusCode());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
