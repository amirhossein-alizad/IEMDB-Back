package com.iemdb.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemdb.Command.AddActorCommand;
import com.iemdb.Command.AddCommentCommand;
import com.iemdb.Command.AddMovieCommand;
import com.iemdb.Command.AddUserCommand;
import com.iemdb.Entity.Actor;
import com.iemdb.Entity.Comment;
import com.iemdb.Entity.Movie;
import com.iemdb.Entity.User;
import com.iemdb.Repository.ActorRepository;
import com.iemdb.Repository.CommentRepository;
import com.iemdb.Repository.MovieRepository;
import com.iemdb.Repository.UserRepository;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class Initializer {

    private static final String TEMPLATES_PATH = "src/main/resources/templates/";
    private static final String MOVIES_API = "http://138.197.181.131:5000/api/v2/movies";
    private static final String ACTORS_API = "http://138.197.181.131:5000/api/v2/actors";
    private static final String USERS_API = "http://138.197.181.131:5000/api/users";
    private static final String COMMENTS_API = "http://138.197.181.131:5000/api/comments";

    private final ObjectMapper objectMapper;
    private DataBase dataBase;
    private JSONParser jsonParser;


    private final ActorRepository actorRepository;
    private final MovieRepository movieRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public Initializer(DataBase dataBase, ActorRepository actorRepository, MovieRepository movieRepository,
                       CommentRepository commentRepository, UserRepository userRepository) {
        this.dataBase = dataBase;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        objectMapper = new ObjectMapper();
        jsonParser = new JSONParser();
        this.actorRepository = actorRepository;
        this.movieRepository = movieRepository;
    }

    public void getDataFromAPI() {
        try {
            getActorsFromService();
            getMoviesFromService();
            getUsersFromService();
            getCommentsFromService();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private JSONArray getDataFromURL(String url) throws Exception {
        Document doc = Jsoup.connect(url).ignoreContentType(true).get();
        String[] s = doc.body().childNodes().get(0).toString().split("\n");
        return (JSONArray) jsonParser.parse(s[1]);
    }

    private void getMoviesFromService() throws Exception {
        JSONArray jsonArray = getDataFromURL(MOVIES_API);
        for (Object jsonObject : jsonArray) {
            try {
                addMovie(jsonObject.toString());
            } catch (Exception e) {}
        }
    }

    private void getActorsFromService() throws Exception {
        JSONArray jsonArray = getDataFromURL(ACTORS_API);
        for (Object jsonObject : jsonArray) {
            try {
                addActor(jsonObject.toString());
            }catch (Exception e) {}
        }
    }

    private void getUsersFromService() throws Exception {
        JSONArray jsonArray = getDataFromURL(USERS_API);
        for (Object jsonObject : jsonArray) {
            try {
                addUser(jsonObject.toString());
            } catch (Exception e) {}
        }
    }

    private void getCommentsFromService() throws Exception {
        JSONArray jsonArray = getDataFromURL(COMMENTS_API);
        for (Object jsonObject : jsonArray) {
            try {
                addComment(jsonObject.toString());
            } catch (Exception e) {}
        }
    }

    private void addActor(String json) throws Exception {
        AddActorCommand addActorCommand = new AddActorCommand();
        addActorCommand.execute(json, objectMapper);
        Actor actor = objectMapper.readValue(json, Actor.class);
        dataBase.addActor(actor);
        actorRepository.save(actor);

    }

    private void addMovie(String json) throws Exception {
        AddMovieCommand addMovieCommand = new AddMovieCommand();
        addMovieCommand.execute(json, objectMapper);
        Movie movie = objectMapper.readValue(json, Movie.class);
        addMovieCommand.checkActors(dataBase.getActors(), movie.getCast());
        dataBase.addMovie(movie);
        movieRepository.save(movie);
    }

    private void addUser(String json) throws Exception {
        AddUserCommand addUserCommand = new AddUserCommand();
        addUserCommand.execute(json, objectMapper);
        dataBase.addUser(objectMapper.readValue(json, User.class));
        userRepository.save(objectMapper.readValue(json, User.class));
    }

    private void addComment(String json) throws Exception {
        AddCommentCommand addCommentCommand = new AddCommentCommand();
        addCommentCommand.execute(json, objectMapper);
        Comment comment = objectMapper.readValue(json, Comment.class);
        addCommentCommand.checkMovie(dataBase.getMovies(), comment.getMovieId());
        addCommentCommand.checkUser(dataBase.getUsers(), comment.getUserEmail());
        dataBase.addComment(comment);
        commentRepository.save(comment);
    }
}
