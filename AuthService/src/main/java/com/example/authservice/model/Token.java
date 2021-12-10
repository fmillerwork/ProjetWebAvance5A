package com.example.authservice.model;

import java.time.Instant;
import java.util.*;

public class Token{
    private String value;
    private Instant startTime;

    public Token() {
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
        return value.length() != 10; // To Complete
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

        } while (isAttributed);

        return generatedValue;
    }

}
