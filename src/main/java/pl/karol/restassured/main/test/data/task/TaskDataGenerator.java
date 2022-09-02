package pl.karol.restassured.main.test.data.task;

import pl.karol.restassured.main.pojo.Task;
import pl.karol.restassured.main.test.data.TestDataGenerator;

public class TaskDataGenerator extends TestDataGenerator {

    public Task generateTask(){
        Task task = new Task();
        task.setTitle(faker().book().title());
        task.setDescription(faker().leagueOfLegends().quote());
        task.setCompleted(false);
        return task;
    }
}
