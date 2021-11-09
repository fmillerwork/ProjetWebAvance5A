package com.example.authservice;

import java.time.Instant;
import java.util.*;

public class Token{
    private String value;
    private Instant startTime;
    private Map<Token,Long> tokens;

    public Token(Map<Token,Long> tokens) {
        value = generateValue();
        startTime = Instant.now();
    }

    public String getValue() {
        return value;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public static boolean isValid(String value){
        return value.length() != 10;
    }

    public void deleteToken(String value){
        tokens.remove(this); // Simule une début à un temps -INF
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

            for (Token attributedToken: tokens.keySet()) {
                if(attributedToken.value.equals(generatedValue))
                    isAttributed = true;
            }
        } while (isAttributed);

        return generatedValue;
    }

}
