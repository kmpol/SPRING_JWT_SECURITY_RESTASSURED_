package pl.karol.restassured.main.rop.user;

import org.apache.http.HttpStatus;
import pl.karol.restassured.main.pojo.User;
import pl.karol.restassured.main.request.configuration.RequestConfigurationBuilder;
import pl.karol.restassured.main.rop.BaseEndpoint;

import java.lang.reflect.Type;

import static io.restassured.RestAssured.given;

public class CreateUserEndpoint extends BaseEndpoint<CreateUserEndpoint, User> {

    private User user;

    @Override
    protected Type getModelType() {
        return User.class;
    }

    @Override
    public CreateUserEndpoint sendRequest() {
        response = given().spec(RequestConfigurationBuilder.getDefaultRequestSpecification()).body(user)
                .when().post("api/register");
        return this;
    }

    @Override
    public int getSuccessStatusCode() {
        return HttpStatus.SC_CREATED;
    }

    public CreateUserEndpoint setUser(User user){
        this.user = user;
        return this;
    }
}
