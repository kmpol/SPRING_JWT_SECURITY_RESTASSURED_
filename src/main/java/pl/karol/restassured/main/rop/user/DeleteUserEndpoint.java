package pl.karol.restassured.main.rop.user;

import org.apache.http.HttpStatus;
import pl.karol.restassured.main.request.configuration.RequestConfigurationBuilder;
import pl.karol.restassured.main.rop.BaseEndpoint;

import java.lang.reflect.Type;

import static io.restassured.RestAssured.given;

public class DeleteUserEndpoint extends BaseEndpoint<DeleteUserEndpoint, String> {

    private String jwt_token;
    @Override
    protected Type getModelType() {
        return String.class;
    }

    @Override
    public DeleteUserEndpoint sendRequest() {
        response =
                given().spec(RequestConfigurationBuilder.getDefaultRequestSpecification())
                        .and().header("Authorization", "Bearer " + jwt_token)
                        .when().delete("api/users");
        return this;
    }

    @Override
    public int getSuccessStatusCode() {
        return HttpStatus.SC_OK;
    }

    public DeleteUserEndpoint setToken(String token){
        this.jwt_token = token;
        return this;
    }
}
