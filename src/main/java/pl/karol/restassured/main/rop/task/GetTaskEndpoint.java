package pl.karol.restassured.main.rop.task;

import org.apache.http.HttpStatus;
import pl.karol.restassured.main.pojo.Task;
import pl.karol.restassured.main.request.configuration.RequestConfigurationBuilder;
import pl.karol.restassured.main.rop.BaseEndpoint;

import java.lang.reflect.Type;

import static io.restassured.RestAssured.given;

public class GetTaskEndpoint extends BaseEndpoint<GetTaskEndpoint, Task> {

    private String token;
    private Long taskId;

    @Override
    protected Type getModelType() {
        return Task.class;
    }

    @Override
    public GetTaskEndpoint sendRequest() {

        response = given().spec(RequestConfigurationBuilder.getDefaultRequestSpecification())
                .header("Authorization", "Bearer " + token)
                .pathParam("taskId", taskId)
                .when().get("api/users/tasks/{taskId}");
        return this;
    }

    @Override
    public int getSuccessStatusCode() {
        return HttpStatus.SC_OK;
    }

    public GetTaskEndpoint setToken(String token){
        this.token = token;
        return this;
    }

    public GetTaskEndpoint setTaskId(Long taskId){
        this.taskId = taskId;
        return this;
    }
}
