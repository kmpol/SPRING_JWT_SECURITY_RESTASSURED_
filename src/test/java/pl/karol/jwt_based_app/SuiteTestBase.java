package pl.karol.jwt_based_app;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.aeonbits.owner.ConfigFactory;
import org.testng.annotations.BeforeSuite;
import pl.karol.jwt_based_app.properties.EnvironmentConfig;

public class SuiteTestBase {

    @BeforeSuite
    public void setupConfiguration(){
        EnvironmentConfig config = ConfigFactory.create(EnvironmentConfig.class);

        RestAssured.baseURI = config.baseUri();
        RestAssured.port = Integer.parseInt(config.basePort());
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }
}
