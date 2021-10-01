package com.example.ProfileService;

import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class ProfileController {

    private final AtomicLong counter = new AtomicLong();
    private final Map<Long, Profile> profiles = new HashMap<Long, Profile>();

    @GetMapping("/PS/profiles")
    public Collection<Profile> profiles() {
        return profiles.values();
    }

    @PutMapping("/PS/profiles")
    public Profile profiles_put(@RequestBody Profile profile) {
        long new_id = counter.incrementAndGet();
        profile.setId(new_id);
        profiles.put(new_id, profile);
        return profile;
    }

    @DeleteMapping("/PS/profiles/{id}")
    public void profile_delete(@PathVariable(value = "id") long id) {
        profiles.remove(id);
    }
}
