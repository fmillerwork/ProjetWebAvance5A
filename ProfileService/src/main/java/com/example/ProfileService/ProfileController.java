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

// @PathVariable => dans le chemin vers le endpoint /PS/profiles/{id}/name (id)
// @RequestBody => dans le corps de la requète /PS/profiles/{id}/name (name) (utile pour envoyer des objets)
// @RequestParam => dans les paramètres de la requète
// Post => Ajout
// Put => Update

@RestController
public class ProfileController {

    private final AtomicLong counter = new AtomicLong();
    private final Map<Long,Profile> profiles = new HashMap<Long,Profile>();

    private Logger logger = LoggerFactory.getLogger(ProfileController.class);

    //Documentée
    @GetMapping("/PS/profiles")
    public Collection<Profile> getProfiles(){
        logger.trace("GET /PS/profiles");
        return profiles.values();
    }

    //Documentée
    @GetMapping("/PS/profiles/{id}")
    public Profile getProfileById(@PathVariable(value = "id") long id){
        logger.trace("GET /PS/profiles/{id}");
        if(!profiles.containsKey(id)) throw new ProfileNotFoundException(id);
        return profiles.get(id);
    }

//    @GetMapping("/PS/profiles/{email}")
//    public Profile getProfileByEmail(@PathVariable(value = "email") String email){
//        logger.trace("GET /PS/profiles/{email}");
//        for (Profile p: profiles.values()) {
//           if(p.getEmail().equals(email))
//               return p;
//        }
//        throw new ProfileNotFoundException(email);
//    }

    //Documentée
    @GetMapping("/PS/profiles/{id}/name")
    public String getProfileNameById(@PathVariable(value = "id") long id){
        logger.trace("GET /PS/profiles/{id}/name");
        return profiles.get(id).getName();
    }

    //Documentée
    @GetMapping("/PS/profiles/{id}/description")
    public String getProfileDescriptionById(@PathVariable(value = "id") long id){
        logger.trace("GET /PS/profiles/{id}/name");
        return profiles.get(id).getDescription();
    }

    //Documentée
    @PostMapping("/PS/profiles")
    public Profile saveProfile(@RequestBody @Valid Profile profile){
        logger.trace("POST /PS/profiles");
        for (Profile p : profiles.values()) {
            if(p.getEmail().equals(profile.getEmail()))
                throw new EmailInUseException();
        }
        long new_id = counter.incrementAndGet();
        profile.setId(new_id);
        profiles.put(new_id, profile);
        logger.info(String.format("Profile created : [%d] %s", new_id, profile.getEmail()));
        return profile;
    }

    //Documentée
    @PutMapping("/PS/profiles/{id}/name")
    public void updateProfileName(@PathVariable(value = "id") long id, @RequestBody String name){
        logger.trace("PUT /PS/profiles/{id}/name");
        if(!profiles.containsKey(id)) throw new ProfileNotFoundException(id);{
            logger.info(String.format("Profile name updated : [%d] %s => [%d] %s", id, profiles.get(id).getName(), id, name));
            profiles.get(id).setName(name);
        }
    }

    //Documentée
    @PutMapping("/PS/profiles/{id}/description")
    public void updateProfileDescription(@PathVariable(value = "id") long id, @RequestBody String description){
        logger.trace("PUT /PS/profiles/{id}/description");
        if(!profiles.containsKey(id)) throw new ProfileNotFoundException(id);{
            logger.info(String.format("Profile description updated : [%d] %s => [%d] %s", id, profiles.get(id).getDescription(), id, description));
            profiles.get(id).setName(description);
        }
    }

    //Documentée
    @DeleteMapping("/PS/profiles/{id}")
    public void deleteProfile(@PathVariable(value = "id") long id){
        logger.trace("DELETE /PS/profiles/{id}");
        if(!profiles.containsKey(id)) throw new ProfileNotFoundException(id);
        logger.info(String.format("Profile deleted : [%d] %s", id, profiles.get(id).getEmail()));
        profiles.remove(id);
    }
}
