package pl.karol.restassured.main.rop.task;

import org.apache.http.HttpStatus;
import pl.karol.restassured.main.pojo.ApiResponse;
import pl.karol.restassured.main.request.configuration.RequestConfigurationBuilder;
import pl.karol.restassured.main.rop.BaseEndpoint;

import java.lang.reflect.Type;

import static io.restassured.RestAssured.given;

public class PatchTaskEndpoint extends BaseEndpoint<PatchTaskEndpoint, ApiResponse> {

    private Long taskId;
    private String token;

    @Override
    protected Type getModelType() {
        return ApiResponse.class;
    }

    @Override
    public PatchTaskEndpoint sendRequest() {
        response = given().spec(RequestConfigurationBuilder.getDefaultRequestSpecification()).
                when().header("Authorization", "Bearer " + token).pathParam("taskId", taskId).patch("api/users/tasks/{taskId}");
        return this;
    }

    @Override
    public int getSuccessStatusCode() {
        return HttpStatus.SC_NO_CONTENT;
    }

    public PatchTaskEndpoint setToken(String token){
        this.token = token;
        return this;
    }

    public PatchTaskEndpoint setTaskId(Long taskId){
        this.taskId = taskId;
        return this;
    }
}
