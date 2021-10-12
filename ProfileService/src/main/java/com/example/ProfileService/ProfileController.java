package com.example.ProfileService;

import com.example.ProfileService.exception.EmailInUseException;
import com.example.ProfileService.exception.ProfileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


@RestController
public class ProfileController {

    private final AtomicLong counter = new AtomicLong();
    private final Map<Long, Profile> profiles = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @GetMapping("/PS/profiles")
    public Collection<Profile> profiles() {
        logger.trace("GET /PS/profiles");
        return profiles.values();
    }

    @PutMapping("/PS/profiles")
    public Profile profiles_put(@RequestBody @Valid Profile profile) {
        long new_id = counter.incrementAndGet();

        for(Profile p: profiles.values()) {
            if(p.getEmail().equals(profile.getEmail())) {
                throw new EmailInUseException(p.getEmail());
            }
        }
        profile.setId(new_id);
        profiles.put(new_id, profile);
        logger.info(String.format("Profile created : [%d] %s",new_id,profile.getEmail()));
        return profile;
    }

    @DeleteMapping("/PS/profiles/{id}")
    public void profile_delete(@PathVariable(value = "id") long id) {
        if(!profiles.containsKey(id)) throw new ProfileNotFoundException(id);
        profiles.remove(id);
        logger.info(String.format("Profile deleted : [%d]",id));
    }

    @GetMapping("/PS/profiles/{id}")
    public Profile profile_get_id(@PathVariable(value = "id") long id) {
        if(!profiles.containsKey(id)) throw new ProfileNotFoundException(id);
        logger.info(String.format("Here's the id : [%d]",id));
        return profiles.get(id);
    }

    @GetMapping("/PS/profiles/{id}/name")
    public String profile_get_name(@PathVariable(value = "id") long id) {
        if(!profiles.containsKey(id)) throw new ProfileNotFoundException(id);
        logger.info(String.format("Here's the name : [%s]",profiles.get(id).getName()));
        return profiles.get(id).getName();
    }

    @PutMapping("/PS/profiles/{id}/name")
    public void profile_put_name(@PathVariable(value = "id") long id, @RequestBody String name) {
        Profile p = profiles.get(id);
        logger.info(String.format("[%d] : %s => [%d] : %s",id,profiles.get(id).getName(),id,name));
        p.setName(name);
    }
}
