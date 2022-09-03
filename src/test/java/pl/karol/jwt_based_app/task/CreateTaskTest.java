package pl.karol.jwt_based_app.task;

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
import pl.karol.restassured.main.rop.user.CreateUserEndpoint;
import pl.karol.restassured.main.rop.user.DeleteUserEndpoint;
import pl.karol.restassured.main.rop.user.LoginUserEndpoint;
import pl.karol.restassured.main.test.data.task.TaskDataGenerator;
import pl.karol.restassured.main.test.data.user.UserTestDataGenerator;

public class CreateTaskTest extends SuiteTestBase {

    User user;
    Task task;
    Task actualTask;
    LoginResponse loginResponse;

    @BeforeMethod
    public void beforeTest(){
        user = new UserTestDataGenerator().generateUser();
        new CreateUserEndpoint().setUser(user).sendRequest().assertRequestSuccess();
        loginResponse = new LoginUserEndpoint().setUsername(user.getUsername()).setPassword(user.getPassword())
                .sendRequest().assertRequestSuccess().getResponseModel();
    }

    @Test
    public void givenTaskAndJwtTokenOfTaskCreatorWhenPostTaskThenTaskCreatedAndOwnerIsSetTest(){
        task = new TaskDataGenerator().generateTask();
        actualTask = new CreateTaskEndpoint().setTask(task).setToken(loginResponse.getAccess_token()).sendRequest().assertRequestSuccess().getResponseModel();

        Assertions.assertThat(task).describedAs("Tasks should be the same").usingRecursiveComparison().ignoringFields("id").isEqualTo(actualTask);
    }

    //TODO: COMMIT THIS TO GIT AND GITHUB

    @AfterMethod
    public void cleanUp(){
        new DeleteTaskEndpoint().setTaskId(actualTask.getId()).setToken(loginResponse.getAccess_token()).sendRequest().assertRequestSuccess();
        new DeleteUserEndpoint().setToken(loginResponse.getAccess_token()).sendRequest().assertRequestSuccess();
    }
}
