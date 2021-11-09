package com.example.AuthService;

public class Token {

    private String value;

    public static String generate(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder pass = new StringBuilder();
        for(int x=0;x<length;x++)   {
            int i = (int)Math.floor(Math.random() * (chars.length() -1));
            pass.append(chars.charAt(i));
        }
        return pass.toString();
    }

    public Token(int length) {
        this.value = generate(length);
    }

    public String getValue() {
        return value;
    }
}
