package co.jrstudios.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class JwtUtil {

    private static final String SECRET = "SDDIYZzY1LFLD2tlYlOD.ySzV1zI0GkNMNYYNzkU0SMkGUYVU";
    private static final long EXPIRATION_TIME = 864_000_000; // 10 days in milliseconds
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    public static String generateToken(String username) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(algorithm);
    }

    public static String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            return verifier.verify(token).getSubject();
        } catch (JWTVerificationException exception) {
            return null; // Token is invalid
        }
    }

    public static boolean validateAuthorizationRequest(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            log.info("JWT verification process started");
            return token.equals(verifier.verify(token).getToken());
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    public static String getUsernameFromToken(String token) {
        return JWT.decode(token).getSubject();
    }
}
