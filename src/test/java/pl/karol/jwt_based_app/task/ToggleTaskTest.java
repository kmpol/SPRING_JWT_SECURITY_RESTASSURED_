package pl.karol.jwt_based_app.task;

import org.aspectj.lang.annotation.After;
import org.assertj.core.api.Assertions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.karol.jwt_based_app.SuiteTestBase;
import pl.karol.restassured.main.pojo.LoginResponse;
import pl.karol.restassured.main.pojo.Task;
import pl.karol.restassured.main.pojo.User;
import pl.karol.restassured.main.rop.task.CreateTaskEndpoint;
import pl.karol.restassured.main.rop.task.DeleteTaskEndpoint;
import pl.karol.restassured.main.rop.task.GetTaskEndpoint;
import pl.karol.restassured.main.rop.task.PatchTaskEndpoint;
import pl.karol.restassured.main.rop.user.CreateUserEndpoint;
import pl.karol.restassured.main.rop.user.DeleteUserEndpoint;
import pl.karol.restassured.main.rop.user.LoginUserEndpoint;
import pl.karol.restassured.main.test.data.task.TaskDataGenerator;
import pl.karol.restassured.main.test.data.user.UserTestDataGenerator;

public class ToggleTaskTest extends SuiteTestBase {

    private User user;
    private LoginResponse loginResponse;
    private Task taskBeforeUpdate;


    @BeforeMethod
    public void beforeMethod(){
        user = new UserTestDataGenerator().generateUser();
        new CreateUserEndpoint().setUser(user).sendRequest().assertRequestSuccess();
        loginResponse = new LoginUserEndpoint().setUsername(user.getUsername()).setPassword(user.getPassword()).sendRequest().assertRequestSuccess().getResponseModel();
        Task task = new TaskDataGenerator().generateTask();
        taskBeforeUpdate = new CreateTaskEndpoint().setTask(task).setToken(loginResponse.getAccess_token()).sendRequest().assertRequestSuccess().getResponseModel();
    }

    @Test
    public void givenTaskWhenToggleTaskCompletedThenTaskIsUpdatedTest(){
        new PatchTaskEndpoint().setTaskId(taskBeforeUpdate.getId()).setToken(loginResponse.getAccess_token()).sendRequest().assertRequestSuccess();
        Task taskAfterUpdate = new GetTaskEndpoint().setToken(loginResponse.getAccess_token()).setTaskId(taskBeforeUpdate.getId()).sendRequest().assertRequestSuccess().getResponseModel();
        Assertions.assertThat(taskAfterUpdate.getCompleted()).describedAs("These values should be opposite!").isEqualTo(!taskBeforeUpdate.getCompleted());
    }

    @AfterMethod
    public void cleanUpAfterTest(){
        new DeleteTaskEndpoint().setToken(loginResponse.getAccess_token()).setTaskId(taskBeforeUpdate.getId()).sendRequest().assertRequestSuccess();
        new DeleteUserEndpoint().setToken(loginResponse.getAccess_token()).sendRequest().assertRequestSuccess();
    }

}
