package it.unipd.stage.sl.lib;

import java.util.Base64;
import java.util.Formatter;

public class Utils {

    public static String toHex(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    public static String toBase64(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes());
    }

    public static String fromBase64(String s) {
        return new String(Base64.getDecoder().decode(s.getBytes()));
    }
}
