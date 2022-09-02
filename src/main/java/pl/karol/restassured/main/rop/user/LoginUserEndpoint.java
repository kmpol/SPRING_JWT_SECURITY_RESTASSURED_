package pl.karol.restassured.main.rop.user;

import groovy.util.logging.Log;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import pl.karol.restassured.main.pojo.LoginResponse;
import pl.karol.restassured.main.request.configuration.RequestConfigurationBuilder;
import pl.karol.restassured.main.rop.BaseEndpoint;

import java.lang.reflect.Type;

import static io.restassured.RestAssured.given;

public class LoginUserEndpoint extends BaseEndpoint<LoginUserEndpoint, LoginResponse> {

    private String username;
    private String password;


    @Override
    protected Type getModelType() {
        return LoginResponse.class;
    }

    @Override
    public LoginUserEndpoint sendRequest() {
        response = given().spec(RequestConfigurationBuilder.getDefaultRequestSpecification().contentType(ContentType.URLENC))
                .formParam("username", username)
                .formParam("password", password)
                .when().post("login");
        return this;
    }

    @Override
    public int getSuccessStatusCode() {
        return HttpStatus.SC_OK;
    }

    public LoginUserEndpoint setUsername(String username){
        this.username=username;
        return this;
    }

    public LoginUserEndpoint setPassword(String password){
        this.password=password;
        return this;
    }
}
