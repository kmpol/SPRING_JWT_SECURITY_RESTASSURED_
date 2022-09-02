package pl.karol.jwt_based_app.properties;

import org.aeonbits.owner.Config;

@Config.Sources("classpath:EnvironmentConfig.properties")
public interface EnvironmentConfig extends Config {

    @Key("BASE_URI")
    String baseUri();

    @Key("BASE_PORT")
    String basePort();

    @Key("JWT_SECRET")
    String jwtSecret();
}
