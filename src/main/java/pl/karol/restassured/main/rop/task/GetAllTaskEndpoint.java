package pl.karol.restassured.main.rop.task;

import org.apache.http.HttpStatus;
import pl.karol.restassured.main.pojo.Task;
import pl.karol.restassured.main.request.configuration.RequestConfigurationBuilder;
import pl.karol.restassured.main.rop.BaseEndpoint;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;

public class GetAllTaskEndpoint extends BaseEndpoint<GetAllTaskEndpoint, Task[]> {

    private String token;

    @Override
    protected Type getModelType() {
        return Task[].class;
    }

    @Override
    public GetAllTaskEndpoint sendRequest() {
        response = given().spec(RequestConfigurationBuilder.getDefaultRequestSpecification())
                .header("Authorization", "Bearer " + token)
                .when().get("api/users/tasks");
        return this;
    }

    @Override
    public int getSuccessStatusCode() {
        return HttpStatus.SC_OK;
    }

    public GetAllTaskEndpoint setToken(String token){
        this.token = token;
        return this;
    }
}
