package com.example.authservice;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class TokenDurationCheckingThread extends Thread{

    private Token token;
    private List<Token> attributedValues;

    public TokenDurationCheckingThread(Token token, List<Token> attributedValues) {
        this.token = token;
        this.attributedValues = attributedValues;
    }

    public void run(){
        while(Duration.between(Instant.now(),token.getStartTime()).compareTo(Duration.ofMinutes(5)) < 0){

        }
        attributedValues.remove(token);
    }

}
