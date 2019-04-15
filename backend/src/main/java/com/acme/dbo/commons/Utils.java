package com.acme.dbo.commons;

import com.google.common.base.Charsets;

import java.util.Random;
import java.util.UUID;

import static com.google.common.hash.Hashing.sha256;

public class Utils {
    private static String alphabet = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890";

    public static String generatorCode(String text) {
        String code = UUID.randomUUID().toString();
        return code;
    }

    public static String genVerifecationCodeShort() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++)
            code.append(alphabet.charAt(new Random().nextInt(alphabet.length())));
        return code.toString();
    }


    public static String sha(String string) {
        return sha256().hashString(string, Charsets.UTF_8).toString();
    }
}
