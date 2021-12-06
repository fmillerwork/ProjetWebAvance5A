package com.example.authservice;

import com.example.authservice.exception.*;
import com.example.authservice.model.Token;
import com.example.authservice.model.TokenDurationCheckingThread;
import com.example.authservice.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Token,Long> tokens = new HashMap<>();
    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @PutMapping("/AS/users")
    public long saveUser(@RequestBody @Valid User user){
        logger.trace("PUT /AS/users");
        for (User u : users.values()) {
            if(u.getId() == user.getId())
                throw new com.example.authservice.exception.IDInUseException(user.getId());
        }
        users.put(user.getId(), user);
        Token token = new Token();
        tokens.put(token,user.getId());
        new TokenDurationCheckingThread(token, tokens).start();
        logger.info(String.format("User created : [%d] => [%s]", user.getId(), user.getPassword()));
        return user.getId();
    }

    @GetMapping("/AS/users/{userId}")
    public User getUserById(@PathVariable(value = "userId") long id){
        logger.trace("GET /AS/users/{userId}");
        if(!users.containsKey(id)) throw new NotFoundUserException(id);
        return users.get(id);
    }

    @DeleteMapping("/AS/users/{userId}")
    public void deleteUser(@PathVariable(value = "userId") long id, @RequestHeader("X-Token") String tokenValue){
        logger.trace("DELETE /AS/users/{userId}");

        checkToken(id, tokenValue);

        logger.info(String.format("User deleted : [%d]", id));
        users.remove(id);
        for (Token t: tokens.keySet()) {
            if(t.getValue().equals(tokenValue))
                tokens.remove(t);
        }
    }

    @PutMapping("/AS/users/{userId}/password")
    public void updateUserPassword(@PathVariable(value = "userId") long id, @RequestHeader("X-Token") String tokenValue, @RequestBody String password){
        logger.trace("PUT /AS/users/{userId}/password");

        checkToken(id, tokenValue);

        logger.info(String.format("Password updated for user : [%d]", id));
        users.get(id).setPassword(password);
    }

    @PostMapping("/AS/users/{userId}/token")
    public String userConnection(@PathVariable(value = "userId") long id, @RequestBody String password){
        logger.trace("POST /AS/users/{userId}/token");
        if(!users.containsKey(id)) throw new NotFoundUserException(id);
        for (User u : users.values()) {
            if(u.getId() == id && u.getPassword().equals(password)){
                Token token;
                do{
                    token = new Token();
                }while(tokenIsAssigned(token));

                tokens.put(token,u.getId());
                new TokenDurationCheckingThread(token, tokens).start();

                logger.info(String.format("User connected : [%d] => [%s]", id, token.getValue()));
                return token.getValue();
            }
        }
        throw new WrongPasswordException(id);
    }

    @DeleteMapping("/AS/users/{userId}/token")
    public void userDisconnection(@PathVariable(value = "userId") long id, @RequestHeader("X-Token") String tokenValue){
        logger.trace("DELETE /AS/users/{userId}/token");

        checkToken(id, tokenValue);

        List<Token> tokenToRemove = new ArrayList<>();
        for(Token token: tokens.keySet()){
            if(tokens.get(token) == id){
                tokenToRemove.add(token);
            }
        }
        for (Token token: tokenToRemove) {
            tokens.remove(token);
        }
        logger.info(String.format("Tokens deleted for user : [%d]", id));
    }

    @GetMapping("/token")
    public long checkTokenExistence(@RequestHeader("X-Token") String tokenValue){
        logger.trace("GET /token");

        for(Token token: tokens.keySet()){
            if(token.getValue().equals(tokenValue)){
                return tokens.get(token);
            }
        }
        throw new InvalidTokenException(tokenValue);
    }

    private void checkToken(@PathVariable("id") long id, @RequestHeader("X-Token") String tokenValue) {
        if (Token.isValid(tokenValue)) throw new InvalidTokenException(tokenValue);

        boolean doesTokenExists = false;
        for (Token token : tokens.keySet()) {
            if (token.getValue().equals(tokenValue)) {
                doesTokenExists = true;
                if (tokens.get(token) != id) throw new WrongUserException(token.getValue());
                break;
            }
        }
        if (!doesTokenExists) throw new NotFoundUserException(id);
    }

    private boolean tokenIsAssigned(Token token){
        for (Token attributedToken: tokens.keySet()) {
            if(attributedToken.getValue().equals(token.getValue()))
                return true;
        }
        return false;
    }
}