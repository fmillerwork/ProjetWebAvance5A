package com.example.ProfileService;

import com.example.ProfileService.exception.*;
import com.example.ProfileService.model.AuthServiceUser;
import com.example.ProfileService.model.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.PropertyAccessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

// @PathVariable => dans le chemin vers le endpoint /PS/profiles/{id}/name (id)
// @RequestBody => dans le corps de la requète /PS/profiles/{id}/name (name) (utile pour envoyer des objets)
// @RequestParam => dans les paramètres de la requète /PS/profiles/{id}/name="nom" (nom)
// Post => Ajout
// Put => Update

@RestController
public class ProfileController {

    private final AtomicLong counter = new AtomicLong();
    private final Map<Long, Profile> profiles = new HashMap<>();

    private RestTemplate restTemplate;

    public ProfileController(RestTemplateBuilder restTemplateBuilder){
        restTemplate = restTemplateBuilder.build();
    }
    @Value("${service.authentification}")
    private String auth_service_url;

    private Logger logger = LoggerFactory.getLogger(ProfileController.class);

    //renvoie l'ensemble des profils
    @GetMapping("/PS/profiles")
    @CrossOrigin
    public Collection<Profile> getProfiles(){
        logger.trace("GET /PS/profiles");
        return profiles.values();
    }

    //renvoie le profil d'id id 
    @GetMapping("/PS/profiles/{id}")
    @CrossOrigin
    public Profile getProfileById(@PathVariable(value = "id") long id){
        logger.trace("GET /PS/profiles/{id}");
        if(!profiles.containsKey(id)) throw new ProfileNotFoundException(id);
        return profiles.get(id);
    }



    //renvoie le nom d'un profil à partir de son id
    @GetMapping("/PS/profiles/{id}/name")
    @CrossOrigin
    public String getProfileNameById(@PathVariable(value = "id") long id){
        logger.trace("GET /PS/profiles/{id}/name");
        return profiles.get(id).getName();
    }

    //renvoie la description d'un profil à partir de son id
    @GetMapping("/PS/profiles/{id}/description")
    @CrossOrigin
    public String getProfileDescriptionById(@PathVariable(value = "id") long id){
        logger.trace("GET /PS/profiles/{id}/name");
        return profiles.get(id).getDescription();
    }

    //sauvegarde un nouveau profil en passant par AuthService et lui attribue un nouvel id 
    @PostMapping("/PS/profiles")
    @CrossOrigin
    public Profile saveProfile(@RequestBody @Valid Profile profile){
        logger.trace("POST /PS/profiles");
        CheckForEmailUse(profile.getEmail());

        long new_id = counter.incrementAndGet();
        profile.setId(new_id);

        // Lien avec AuthService
        AuthServiceUser auth_service_user = new AuthServiceUser(new_id);
        restTemplate.put(
                auth_service_url + "/AS/users",
                auth_service_user);

        profiles.put(new_id, profile);
        logger.info(String.format("Profile created : [%d] %s", new_id, profile.getEmail()));
        return profile;
    }

    /*Update le nom d'un profil d'id
     ProfileNotFoundException dans le cas où on ne trouve pas de profil correspondant à l'id en argument
    */
    
    @PutMapping("/PS/profiles/{id}/name")
    @CrossOrigin
    public void updateProfileName(@PathVariable(value = "id") long id, @RequestBody String name){
        logger.trace("PUT /PS/profiles/{id}/name");
        if(!profiles.containsKey(id)) throw new ProfileNotFoundException(id);{
            logger.info(String.format("Profile name updated : [%d] %s => [%d] %s", id, profiles.get(id).getName(), id, name));
            profiles.get(id).setName(name);
        }
    }

    /*Update la description d'un profil
    ProfileNotFoundException dans le cas où on ne trouve pas de profil correspondant à l'id en argument
    */
    @PutMapping("/PS/profiles/{id}/description")
    @CrossOrigin
    public void updateProfileDescription(@PathVariable(value = "id") long id, @RequestBody String description){
        logger.trace("PUT /PS/profiles/{id}/description");
        if(!profiles.containsKey(id)) throw new ProfileNotFoundException(id);{
            logger.info(String.format("Profile description updated : [%d] %s => [%d] %s", id, profiles.get(id).getDescription(), id, description));
            profiles.get(id).setName(description);
        }
    }

    /*Suppression du profil correspondant à l'id en argument
    tout les tokens associés à ce profil sont supprimés
    */
    @DeleteMapping("/PS/profiles/{id}")
    @CrossOrigin
    public void deleteProfile(@PathVariable(value = "id") long id, @RequestHeader(value="X-Token") String token){
        logger.trace("DELETE /PS/profiles/{id}");

        HttpHeaders header = new HttpHeaders();
        header.add("X-Token", token);
        HttpEntity entity = new HttpEntity<>("", header);
        ResponseEntity<Long>respons = restTemplate.exchange(auth_service_url + "/AS/token", HttpMethod.GET, entity, Long.class);
        Long token_user = respons.getBody();
        if(token_user != id)
            throw new RuntimeException(); // CHANGER EXCEPTION

        if(!profiles.containsKey(id)) throw new ProfileNotFoundException(id);
        logger.info(String.format("Profile deleted : [%d] %s", id, profiles.get(id).getEmail()));
        profiles.remove(id);
    }

    /*Update de l'adresse email d'un profil associé à un id 
    ProfilNotFoundException dans le cas où aucun profil correspondant à l'id n'est trouvé
    */
    @PutMapping("/PS/profiles/{id}/email")
    @CrossOrigin
    public Profile update_email(
            @PathVariable(value = "id") Long id,
            @RequestHeader(value = "X-Token") String token,
            @RequestBody String email) {
        logger.trace(String.format("PUT /PS/profiles/%d/email", id));
        if (!profiles.containsKey(id))
            throw new ProfileNotFoundException(id);
        checkTokenAgainstUser(token, id);
        Profile profile = profiles.get(id);
        CheckForEmailUse(profile.getEmail());
        logger.trace(String.format("Old email: %s.", profile.getEmail()));
        profiles.get(id).setEmail(email);
        logger.trace(String.format("Email changed email: %s.", profile.getEmail()));
        profile.setEmail(email);
        return profile;
    }

    /*login à l'aide d'un email et d'un mot de passe
    Exception dans le cas ou l'adresse email n'est pas trouvée
    Exception dans le cas ou le mot de passe est incorrect
    */
    @PostMapping("/PS/login")
    @CrossOrigin
    public String login(
            @RequestParam(value = "email") String email,
            @RequestBody String password)
    {
        logger.trace(String.format("POST /PS/login?email=%s", email));
        boolean emailExists = false;
        for (Profile p : profiles.values()) {
            if (p.getEmail().equals(email)){
                emailExists = true;
                break;
            }
        }
        if (!emailExists)
            throw new ProfileNotFoundException(email);
        try{
            for (Profile p : profiles.values()) {
                if (p.getEmail().equals(email)) {
                    String token = restTemplate.postForObject(
                            String.format(
                                    "%s/AS/users/%d/token",
                                    auth_service_url, p.getId()),
                            password, String.class);

                    logger.trace(String.format("Profile connecté avec le token : %s.", token));
                    return token;
                }
            }
        }catch(HttpClientErrorException.Unauthorized e){
            throw new WrongPasswordException(email);
        }
        throw new RuntimeException(); // Jamais atteint
    }

    private void CheckForEmailUse(@RequestParam("email") String email) {
        for (Profile p : profiles.values()) {
            if (p.getEmail().equals(email))
                throw new EmailInUseException(email);
        }
    }

    // Renvoi une exception si le token est invalide ou ne correspond pas au bon user
    private void checkTokenAgainstUser(String token, Long user_id) {
        HttpHeaders header = new HttpHeaders();
        header.add("X-Token", token);
        HttpEntity<String> entity = new HttpEntity("", header);
        try {
            ResponseEntity<Long> response = restTemplate.exchange(
                    auth_service_url + "/AS/token",
                    HttpMethod.GET, entity, Long.class);
            Long token_user = response.getBody();
            if (!Objects.equals(token_user, user_id))
                throw new InvalidTokenException(token);
        } catch (HttpClientErrorException.Unauthorized ex) {
            throw new InvalidTokenException(token);
        }
    }
}
