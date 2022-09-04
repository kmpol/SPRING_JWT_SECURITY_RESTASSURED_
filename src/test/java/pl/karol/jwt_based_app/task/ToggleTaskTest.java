package pl.karol.jwt_based_app.task;

import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.karol.jwt_based_app.SuiteTestBase;
import pl.karol.restassured.main.pojo.ApiResponse;
import pl.karol.restassured.main.pojo.LoginResponse;
import pl.karol.restassured.main.pojo.Task;
import pl.karol.restassured.main.pojo.User;
import pl.karol.restassured.main.rop.task.CreateTaskEndpoint;
import pl.karol.restassured.main.rop.task.PatchTaskEndpoint;
import pl.karol.restassured.main.rop.user.CreateUserEndpoint;
import pl.karol.restassured.main.rop.user.LoginUserEndpoint;
import pl.karol.restassured.main.test.data.task.TaskDataGenerator;
import pl.karol.restassured.main.test.data.user.UserTestDataGenerator;

public class ToggleTaskTest extends SuiteTestBase {

    private User user;
    private LoginResponse loginResponse;
    private Task task;


    @BeforeMethod
    public void beforeMethod(){
        user = new UserTestDataGenerator().generateUser();
        new CreateUserEndpoint().setUser(user).sendRequest().assertRequestSuccess();
        loginResponse = new LoginUserEndpoint().setUsername(user.getUsername()).setPassword(user.getPassword()).sendRequest().assertRequestSuccess().getResponseModel();
        task = new TaskDataGenerator().generateTask();
        new CreateTaskEndpoint().setTask(task).setToken(loginResponse.getAccess_token()).sendRequest().assertRequestSuccess().getResponseModel();
    }

    @Test
    public void givenTaskWhenToggleTaskCompletedThenTaskIsUpdatedTest(){
        ApiResponse actualResponse = new PatchTaskEndpoint().setToken(loginResponse.getAccess_token()).setTaskId(task.getId())
                .sendRequest().assertRequestSuccess().getResponseModel();
        ApiResponse response = new ApiResponse();
        response.setCode(HttpStatus.SC_NOT_FOUND);
        Assertions.assertThat(actualResponse).describedAs("Should return 204").usingRecursiveComparison().isEqualTo(response);
        //TODO: get that task by id, then check its completion that is reversed.

    }

}
