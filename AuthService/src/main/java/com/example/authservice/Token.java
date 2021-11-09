package com.example.authservice;

import java.time.Instant;
import java.util.*;

public class Token{
    private String value;
    private Instant startTime;
    private static List<Token> attributedTokens = new ArrayList<>();
    private TokenDurationCheckingThread TDCT;

    public Token() {
        value = generateValue();
        attributedTokens.add(this);
        startTime = Instant.now();
        TDCT = new TokenDurationCheckingThread(this, attributedTokens);
        TDCT.start();
    }

    public String getValue() {
        return value;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public static List<Token> getAttributedTokens() {
        return attributedTokens;
    }

    public void deleteToken(String value){
        startTime = Instant.MIN; // Simule une début à un temps -INF
    }

    private String generateValue(){
        int leftLimit = 48; // numeral '0'
        int rightLimit = 90; // letter 'Z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedValue = "";
        boolean isAttributed = false;
        do {
            generatedValue = random.ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            for (Token attributedToken: attributedTokens) {
                if(attributedToken.value.equals(generatedValue))
                    isAttributed = true;
            }
        } while (isAttributed);

        return generatedValue;
    }

}
