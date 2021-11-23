package com.example.AuthService;

import com.example.AuthService.exception.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class UserController {

    private final AtomicLong counter = new AtomicLong();
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, Token> tokens = new HashMap<String, Token>();

    @GetMapping("/AS/users")
    public Collection<User> users(){
        return users.values();
    }

    /**
     * Créer un nouvel user avec un mot de passe
     * @param user the user
     * @return the user
     */
    @PutMapping("/AS/users")
    public User put_user(@RequestBody @Valid User user) {
        if(users.containsKey(user.getId())) throw new UserAlreadyExistsException(user.getId());
        long new_id = counter.incrementAndGet();
        user.setId(new_id);
        user.setTokens(new ArrayList<>());
        users.put(new_id,user);
        return user;
    }

    /**
     * Tester si un user avec cet id existe
     * @param userId the id
     * @return the id of the user
     */
    @GetMapping("/AS/users/{userId}")
    public long get_user_id(@PathVariable long userId){
        if(!users.containsKey(userId)) throw new UserNotFoundException(userId);
        return users.get(userId).getId();
    }

    /**
     * Cela rend tous les token associés à cet user invalides.
     * @param userId the user id to remove
     * @param X_Token the token
     */
    @DeleteMapping("/AS/users/{userId}")
    public void user_delete(@PathVariable(value = "userId")long userId,@RequestHeader(value="X-Token") String X_Token) {
        if (!tokens.containsKey(X_Token))
            throw new TokenNotValidException(X_Token);
        Token t = tokens.get(X_Token);
        if (t.getStartTime().plusSeconds(5*60).compareTo(Instant.now())<0) {
            tokens.remove(t);
            throw new TokenNotValidException(X_Token);
        }
        if (!users.containsKey(userId))
            throw new UserNotFoundException(userId);
        User u = users.get(userId);
        if(u.getTokens().contains(X_Token)) users.remove(userId);
    }

    /**
     * Changer le mot de passe d'un user.
     * @param userId the user id
     * @param X_Token the token
     * @param password the new password
     */
    @PutMapping("/AS/users/{userId}/password")
    public void change_password(@PathVariable(value = "userId")long userId, @RequestHeader String X_Token,@RequestBody String password) {
        User u = users.get(userId);
        if(u.getTokens().contains(X_Token)) u.setPassword(password);
    }

    /**
     * Connexion de l'user
     * @param userId the user id
     * @param password the password
     * @return a new token
     */
    @PutMapping("/AS/users/{userId}/token")
    public String connexion_user(@PathVariable long userId, @RequestBody String password){
        User u = users.get(userId);
        if(u.getPassword().equals(password)){
            Token token = new Token(9);
            u.getTokens().add(token.getValue());
            new TokenDurationCheckThread(token, u).start(); //start the thread when the token is created
            return token.getValue();
        }
        return null;
    }

    /**
     * Déconnexion d'un user
     * @param userId the user id
     * @param X_Token the token to delete
     */
    @DeleteMapping("/AS/users/{userId}/token")
    public void delete_token(@PathVariable(value = "userId")long userId,@RequestHeader String X_Token){
        User u = users.get(userId);
        if(u.getTokens().contains(X_Token)) u.getTokens().clear();
    }

    /**
     * Un token est valide s'il a été créé avec succès par
     *         un post vers /token, s'il n'a pas été créé il y a plus
     *         de 5 minutes, s'il n'a pas été supprimé et si
     *         l'user de ce token existe toujours.
     * @param X_Token the token
     * @return the user id where the token is present
     */
    @GetMapping("/token")
    public long get_token(@RequestHeader(value="X-Token") String X_Token) {
        for (User u : users.values()) {
            if(u.getTokens().contains(X_Token)) return u.getId();
        }

        return 0;
    }
}
