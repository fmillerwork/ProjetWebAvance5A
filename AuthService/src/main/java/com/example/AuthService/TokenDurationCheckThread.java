package com.example.AuthService;

import java.time.Duration;
import java.time.Instant;

/**
 * Class to check if a token is active more than 5 minutes or not
 */
public class TokenDurationCheckThread extends Thread{

    private Token token;
    private User u;

    public TokenDurationCheckThread(Token token, User u) {

        this.token = token;
        this.u = u;
    }

    public void run(){
        while(Duration.between(token.getStartTime(),Instant.now()).compareTo(Duration.ofSeconds(5*60)) < 0){
            //wait 5 minutes before deleting
        }
        u.getTokens().remove(token.getValue());
    }

}
