package com.example.authservice;

import com.example.authservice.exception.IDInUseException;
import com.example.authservice.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    private final Map<Long,User> users = new HashMap<Long,User>();
    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @PutMapping("/AS/users")
    public long saveProfile(@RequestBody @Valid User user){
        logger.trace("PUT /AS/users");
        for (User u : users.values()) {
            if(u.getId() == user.getId())
                throw new IDInUseException();
        }
        users.put(user.getId(), user);
        logger.info(String.format("User created : [%d] => [%s]", user.getId(), user.getPassword()));
        return user.getId();
    }

    @GetMapping("/AS/users/{userId}")
    public User getProfileById(@PathVariable(value = "id") long id){
        logger.trace("GET /AS/users/{userId}");
        if(!users.containsKey(id)) throw new UserNotFoundException(id);
        return users.get(id);
    }

    @DeleteMapping("/AS/users/{userId}")
    public void deleteProfile(@PathVariable(value = "id") long id){
        logger.trace("DELETE /AS/users/{userId}");
        if(!users.containsKey(id)) throw new UserNotFoundException(id);
        logger.info(String.format("User deleted : [%d]", users.get(id)));
        users.remove(id);
    }

    @PutMapping("/AS/users/{userId}")
    public void updateUserEmail(@PathVariable(value = "id") long id, @RequestBody String password){
        logger.trace("PUT /PS/profiles/{id}/name");
        if(!users.containsKey(id)) throw new UserNotFoundException(id);{
            logger.info(String.format("User password updated : [%d] %s => [%d] %s", id, users.get(id).getPassword(), id, password));
            users.get(id).setPassword(password);
        }
    }
}
