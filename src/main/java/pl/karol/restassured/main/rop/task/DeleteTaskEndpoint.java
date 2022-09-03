package pl.karol.restassured.main.rop.task;

import org.apache.http.HttpStatus;
import pl.karol.restassured.main.pojo.ApiResponse;
import pl.karol.restassured.main.request.configuration.RequestConfigurationBuilder;
import pl.karol.restassured.main.rop.BaseEndpoint;

import java.lang.reflect.Type;

import static io.restassured.RestAssured.given;

public class DeleteTaskEndpoint extends BaseEndpoint<DeleteTaskEndpoint, ApiResponse> {

    private Long taskId;
    private String token;

    @Override
    protected Type getModelType() {
        return ApiResponse.class;
    }

    @Override
    public DeleteTaskEndpoint sendRequest() {
        response = given().spec(RequestConfigurationBuilder.getDefaultRequestSpecification())
                .header("Authorization", "Bearer " + token)
                .pathParam("taskId", taskId)
                .when().delete("api/users/tasks/{taskId}");
        return this;
    }

    @Override
    public int getSuccessStatusCode() {
        return HttpStatus.SC_NO_CONTENT;
    }

    public DeleteTaskEndpoint setTaskId(Long taskId){
        this.taskId = taskId;
        return this;
    }

    public DeleteTaskEndpoint setToken(String token){
        this.token = token;
        return this;
    }
}
