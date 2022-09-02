package pl.karol.jwt_based_app.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.aeonbits.owner.ConfigFactory;
import pl.karol.jwt_based_app.exception.ApiBadRequestException;
import pl.karol.jwt_based_app.properties.EnvironmentConfig;

import javax.servlet.http.HttpServletRequest;

public class JwtUtils {

    private static final EnvironmentConfig config = ConfigFactory.create(EnvironmentConfig.class);

    public static Algorithm algorithm = Algorithm.HMAC256(config.jwtSecret().getBytes());

    public static String getUsernameFromTokenInsideRequest (HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String refresh_token = authHeader.substring("Bearer ".length());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(refresh_token);
            String username = decodedJWT.getSubject();
            return username;
        }else {
            throw new ApiBadRequestException("Authorization header is missing or invalid");
        }
    }
}
