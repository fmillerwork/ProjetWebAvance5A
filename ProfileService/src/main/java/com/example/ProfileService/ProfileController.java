package com.example.ProfileService;

import com.example.ProfileService.exception.EmailInUseException;
import com.example.ProfileService.exception.ProfileNotFoundException;
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

    @GetMapping("/PS/profiles")
    public Collection<Profile> getProfiles(){
        return profiles.values();
    }

    @GetMapping("/PS/profiles/{id}")
    public Profile getProfileById(@PathVariable(value = "id") long id){
        if(!profiles.containsKey(id)) throw new ProfileNotFoundException(id);
        return profiles.get(id);
    }

    @GetMapping("/PS/profiles/{id}/name")
    public String getProfileByName(@PathVariable(value = "id") long id){
        return profiles.get(id).getName();
    }

    @PostMapping("/PS/profiles")
    public Profile saveProfile(@RequestBody @Valid Profile profile){
        for (Profile p : profiles.values()) {
            if(p.getEmail().equals(profile.getEmail()))
                throw new EmailInUseException();
        }
        long new_id = counter.incrementAndGet();
        profile.setId(new_id);
        profiles.put(new_id, profile);
        return profile;
    }

    @PutMapping("/PS/profiles/{id}/name")
    public void updateProfileName(@PathVariable(value = "id") long id, @RequestBody String name){
        if(profiles.containsKey(id)) throw new ProfileNotFoundException(id);
            profiles.get(id).setName(name);
    }

    @PutMapping("/PS/profiles/{id}/description")
    public void updateProfileDescription(@PathVariable(value = "id") long id, @RequestBody String description){
        if(profiles.containsKey(id)) throw new ProfileNotFoundException(id);
        profiles.get(id).setName(description);
    }

    @DeleteMapping("/PS/profiles/{id}")
    public void deleteProfile(@PathVariable(value = "id") long id){
        if(profiles.containsKey(id)) throw new ProfileNotFoundException(id);
        profiles.remove(id);
    }
}
