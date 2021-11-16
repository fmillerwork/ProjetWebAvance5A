package com.example.AuthService;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class TokenDurationCheckThread extends Thread{

    private Token token;
    private User u;

    public TokenDurationCheckThread(Token token, User u) {

        this.token = token;
        this.u = u;
    }

    public void run(){
        while(Duration.between(token.getStartTime(),Instant.now()).compareTo(Duration.ofSeconds(5*60)) < 0){
            System.out.println(Duration.between(Instant.now(),token.getStartTime()));
            System.out.println(Duration.ofSeconds(30));
        }
        u.getTokens().remove(token.getValue());
    }

}
