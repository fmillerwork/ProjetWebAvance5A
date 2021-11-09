package com.example.authservice;

import com.example.authservice.exception.IDInUseException;
import com.example.authservice.exception.TokenNotValidException;
import com.example.authservice.exception.UserNotFoundException;
import com.example.authservice.exception.WrongUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    private final Map<Long,User> users = new HashMap<>();
    private final Map<Token,Long> tokens = new HashMap<>();
    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @PutMapping("/AS/users")
    public long saveUser(@RequestBody @Valid User user){
        logger.trace("PUT /AS/users");
        for (User u : users.values()) {
            if(u.getId() == user.getId())
                throw new IDInUseException(user.getId());
        }
        users.put(user.getId(), user);
        Token token = new Token(tokens);
        tokens.put(token,user.getId());
        new TokenDurationCheckingThread(token, tokens).start();
        logger.info(String.format("User created : [;%d] => [%s]", user.getId(), user.getPassword()));
        return user.getId();
    }

    @GetMapping("/AS/users/{userId}")
    public User getUserById(@PathVariable(value = "id") long id){
        logger.trace("GET /AS/users/{userId}");
        if(!users.containsKey(id)) throw new UserNotFoundException(id);
        return users.get(id);
    }

    @DeleteMapping("/AS/users/{userId}")
    public void deleteUser(@PathVariable(value = "id") long id, @RequestHeader("X-Token") String tokenValue){
        logger.trace("DELETE /AS/users/{userId}");

        if(Token.isValid(tokenValue))  throw new TokenNotValidException(tokenValue);

        boolean doesTokenExists = false;
        for(Token token : tokens.keySet()){
            if(token.getValue().equals(tokenValue)){
                doesTokenExists = true;
                if(tokens.get(token) != id) throw new WrongUserException(token.getValue());
                break;
            }
        }
        if(!doesTokenExists)  throw new UserNotFoundException(id);

        logger.info(String.format("User deleted : [%d]", users.get(id)));
        users.remove(id);
    }

    @PutMapping("/AS/users/{userId}/password")
    public void updateUserPassword(@PathVariable(value = "id") long id, @RequestHeader("X-Token") String tokenValue, @RequestBody String password){
        logger.trace("PUT /AS/users/{userId}/password");

        if(Token.isValid(tokenValue))  throw new TokenNotValidException(tokenValue);

        boolean doesTokenExists = false;
        for(Token token : tokens.keySet()){
            if(token.getValue().equals(tokenValue)){
                doesTokenExists = true;
                if(tokens.get(token) != id) throw new WrongUserException(token.getValue());
                break;
            }
        }
        if(!doesTokenExists)  throw new UserNotFoundException(id);

        logger.info(String.format("User deleted : [%d]", users.get(id)));
        users.get(id).setPassword(password);
    }

}
