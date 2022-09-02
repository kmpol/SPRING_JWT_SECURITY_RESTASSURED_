package pl.karol.restassured.main.test.data.user;

import pl.karol.restassured.main.pojo.User;
import pl.karol.restassured.main.test.data.TestDataGenerator;

public class UserTestDataGenerator extends TestDataGenerator {

    public User generateUser(){
        User user = new User();
        user.setFirstName(faker().name().firstName());
        user.setLastName(faker().name().lastName());
        user.setUsername(faker().name().username());
        user.setPassword("Passw0rd123!");
        return user;
    }
}
