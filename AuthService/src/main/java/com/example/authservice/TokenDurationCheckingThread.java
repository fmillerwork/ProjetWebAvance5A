package com.example.authservice;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class TokenDurationCheckingThread extends Thread{

    private Token token;
    private Map<Token,Long> tokens;

    public TokenDurationCheckingThread(Token token, Map<Token,Long> tokens) {
        this.token = token;
        this.tokens = tokens;
    }

    public void run(){
        while(Duration.between(Instant.now(),token.getStartTime()).compareTo(Duration.ofMinutes(5)) < 0){

        }
        tokens.remove(token);
    }

}
