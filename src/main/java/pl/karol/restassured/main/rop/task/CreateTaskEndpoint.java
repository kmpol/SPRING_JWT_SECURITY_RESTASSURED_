package pl.karol.restassured.main.rop.task;

import org.apache.http.HttpStatus;
import pl.karol.restassured.main.pojo.Task;
import pl.karol.restassured.main.request.configuration.RequestConfigurationBuilder;
import pl.karol.restassured.main.rop.BaseEndpoint;

import java.lang.reflect.Type;

import static io.restassured.RestAssured.given;

public class CreateTaskEndpoint extends BaseEndpoint<CreateTaskEndpoint, Task> {

    private Task task;
    private String token;

    @Override
    protected Type getModelType() {
        return Task.class;
    }

    @Override
    public CreateTaskEndpoint sendRequest() {
        given().spec(RequestConfigurationBuilder.getDefaultRequestSpecification()).body(task)
                .header("Authorization", "Bearer " + token)
                .when().post("api/users/tasks");
        return this;
    }

    @Override
    public int getSuccessStatusCode() {
        return HttpStatus.SC_CREATED;
    }

    public CreateTaskEndpoint setTask(Task task){
        this.task = task;
        return this;
    }

    public CreateTaskEndpoint setToken(String token){
        this.token = token;
        return this;
    }
}
