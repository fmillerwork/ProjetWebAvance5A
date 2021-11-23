package com.example.ProfileService;

import com.example.ProfileService.exception.EmailInUseException;
import com.example.ProfileService.exception.ProfileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;


@RestController
public class ProfileController {

    private final AtomicLong counter = new AtomicLong();
    private final Map<Long, Profile> profiles = new HashMap<>();
    private final ArrayList<String> emails = new ArrayList();
    private final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Value("${service.authentication}")
    private String auth_service_url;

    @GetMapping("/PS/profiles")
    public Collection<Profile> profiles() {
        logger.trace("GET /PS/profiles");
        return profiles.values();
    }

    @PutMapping("/PS/profiles")
    public Profile profiles_put(@RequestBody @Valid Profile profile, @RequestHeader(value = "password") String password) {
        long new_id = counter.incrementAndGet();

        for(Profile p: profiles.values()) {
            if(p.getEmail().equals(profile.getEmail())) {
                throw new EmailInUseException(p.getEmail());
            }
        }
        profile.setId(new_id);

        AuthServiceUser auth_service_user = new AuthServiceUser(new_id,password);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put(auth_service_url + "/AS/users",auth_service_user);


        profiles.put(new_id, profile);
        logger.info(String.format("Profile created : [%d] %s",new_id,profile.getEmail()));
        return profile;
    }

    @DeleteMapping("/PS/profiles/{id}")
    public void profile_delete(@PathVariable(value = "id") long id, @RequestHeader(value="X-Token") String token) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders header = new HttpHeaders();
        header.add("X-Token", token);
        HttpEntity<String> entity = new HttpEntity<>("", header);
        ResponseEntity<Long> response = restTemplate.exchange(
                auth_service_url + "/token",
                HttpMethod.GET,entity,Long.class
        );
        Long token_user = response.getBody();
        if(token_user != id){
            throw new RuntimeException();
        }
        if(!profiles.containsKey(id)) throw new ProfileNotFoundException(id);
        profiles.remove(id);
        restTemplate.exchange(
                auth_service_url + "/AS/users/" + id,
                HttpMethod.DELETE,
                entity,
                String.class
        );
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

    private void check_token_against_user(String token, Long user_id){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders header = new HttpHeaders();
        header.add("X-Token",token);
        HttpEntity<String> entity = new HttpEntity<>("",header);
        try {
            ResponseEntity<Long> response = restTemplate.exchange(
                    auth_service_url + "/token",HttpMethod.GET,entity,Long.class
            );
            Long token_user = response.getBody();
            if(!Objects.equals(token_user,user_id)){
                //throw new InvalidTokenException(user_id,token);
            }
        } catch (HttpClientErrorException.Unauthorized e) {
            //throw new InvalidTokenException(user_id,token);
        }
    }

    @PostMapping("/PS/login")
    public void login(@RequestParam(value = "email") String email,@RequestHeader String password){
        /*if(!emails.contains(email)) {
            throw new RuntimeException();
        }*/
        logger.info("Email dans la fonction : " + email);
        for(Profile p : profiles.values()) {
            logger.info("Email checké : " + p.getEmail());
            if(p.getEmail().equals(email)){
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.put(
                        String.format("%s/AS/users/%d/token",auth_service_url,p.getId()),password
                );

            }
        }
        //throw new RuntimeException();
    }
}
