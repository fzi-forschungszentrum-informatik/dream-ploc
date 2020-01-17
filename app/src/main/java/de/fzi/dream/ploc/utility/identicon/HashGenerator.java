package de.fzi.dream.ploc.utility.identicon;

import java.security.MessageDigest;

import static de.fzi.dream.ploc.utility.Constants.IDENTICON_HASH_ALGORITHM;

public class HashGenerator implements HashInterface {
    private MessageDigest mMessageDigest;

    public HashGenerator() {
        try {
            mMessageDigest = MessageDigest.getInstance(IDENTICON_HASH_ALGORITHM);
        } catch (Exception e) {
            System.err.println("Error creating algorithm: " + IDENTICON_HASH_ALGORITHM);
        }
    }

    public byte[] generateHash(String input) {
        return mMessageDigest.digest(input.getBytes());
    }

}
