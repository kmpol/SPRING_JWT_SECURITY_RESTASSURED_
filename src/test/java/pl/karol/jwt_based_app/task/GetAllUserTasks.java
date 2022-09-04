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
import pl.karol.restassured.main.rop.task.GetAllTaskEndpoint;
import pl.karol.restassured.main.rop.user.CreateUserEndpoint;
import pl.karol.restassured.main.rop.user.DeleteUserEndpoint;
import pl.karol.restassured.main.rop.user.LoginUserEndpoint;
import pl.karol.restassured.main.test.data.task.TaskDataGenerator;
import pl.karol.restassured.main.test.data.user.UserTestDataGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetAllUserTasks extends SuiteTestBase {

    private TaskDataGenerator taskDataGenerator = new TaskDataGenerator();

    private User user;
    private LoginResponse loginResponse;
    private List<Task> tasks = new ArrayList<>();

    @BeforeMethod
    public void beforeTest(){
        user = new UserTestDataGenerator().generateUser();
        new CreateUserEndpoint().setUser(user).sendRequest().assertRequestSuccess();
        loginResponse = new LoginUserEndpoint().setUsername(user.getUsername()).setPassword(user.getPassword())
                .sendRequest().assertRequestSuccess().getResponseModel();

        for (int i = 0; i < 3; i++) {
            Task task = taskDataGenerator.generateTask();
            Task savedTask = new CreateTaskEndpoint().setTask(task).setToken(loginResponse.getAccess_token()).sendRequest().assertRequestSuccess().getResponseModel();
            tasks.add(savedTask);
        }
    }

    @Test
    public void givenThreeTasksWhenGetAllUserTasksThenUserShouldHasThreeTasksWhereTaskOwnerIsUserIdTest(){
        List<Task> responseList = Arrays.asList(new GetAllTaskEndpoint().setToken(loginResponse.getAccess_token()).sendRequest().assertRequestSuccess().getResponseModel());
        Assertions.assertThat(responseList.size()).isEqualTo(tasks.size());
        Assertions.assertThat(responseList).usingRecursiveFieldByFieldElementComparator().hasSameElementsAs(tasks);
    }

    @AfterMethod
    public void cleanUpAfterTest(){
        for (int i = 0; i < tasks.size(); i++) {
            new DeleteTaskEndpoint().setToken(loginResponse.getAccess_token()).setTaskId(tasks.get(i).getId()).sendRequest().assertRequestSuccess();
        }
        new DeleteUserEndpoint().setToken(loginResponse.getAccess_token()).sendRequest().assertRequestSuccess();
    }
}
