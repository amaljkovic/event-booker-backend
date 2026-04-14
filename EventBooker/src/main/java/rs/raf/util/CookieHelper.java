package rs.raf.util;

import spark.Request;
import spark.Response;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.UUID;

public class CookieHelper {
    private static final String COOKIE_NAME = "anonimusi";
    private static final String SECRET = "bljaky";

    public static String getOrCreateVisitorId(Request req, Response res) {
        String cookie = req.cookie(COOKIE_NAME);
        if (cookie == null || !isValid(cookie)) {
            String raw = UUID.randomUUID().toString();
            String signed = raw + "." + sign(raw);

            boolean secure = "https".equalsIgnoreCase(req.scheme()); // Secure only on HTTPS
            // Spark's cookie() has no SameSite, so set header manually:
            String attrs = "Path=/; Max-Age=" + (60*60*24*365) + "; HttpOnly; SameSite=Lax";
            if (secure) attrs += "; Secure";
            res.raw().addHeader("Set-Cookie", COOKIE_NAME + "=" + signed + "; " + attrs);

            //res.cookie("/", COOKIE_NAME, signed, 60 * 60 * 24 * 365, true, true);
            return raw;
        } else {
            return cookie.split("\\.")[0];
        }
    }

    public static boolean isValid(String cookie) {
        try {
            String[] parts = cookie.split("\\.");
            return parts.length == 2 && parts[1].equals(sign(parts[0]));
        } catch (Exception e) {
            return false;
        }
    }

    private static String sign(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET.getBytes(), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(value.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Signing failed", e);
        }
    }
}
