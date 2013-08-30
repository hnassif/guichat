package server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Encoding class is used for encoding and decoding the message exchanges
 * between client and the server so that the new line character could be
 * handled correctly when the opening a new bufferedReader and calling
 * in.readLine() on it.
 * 
 * Given more time, we could have used the encoding class to help write JUnit tests
 * to test concurrency and clientHandling and serverHandling.
 *
 */
public class Encoding {

    /**
     * Encodes text using URLEncoder
     *
     * @param text
     *            the text going to be encoded
     * @return the after-encoding text
     */
    public static String encode(String text) {
        String result = "";
        if (text == null) {
            return null;
        }
        try {
            result = URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Decodes text using URLDecoder
     *
     * @param text
     *            the text going to be decoded
     */
    public static String decode(String text) {
        String result = "";
        if (text == null) {
            return null;
        }
        try {
            result = URLDecoder.decode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;

    }

}