package pl.karol.jwt_based_app.user;

import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pl.karol.jwt_based_app.SuiteTestBase;
import pl.karol.restassured.main.pojo.LoginResponse;
import pl.karol.restassured.main.pojo.User;
import pl.karol.restassured.main.rop.user.CreateUserEndpoint;
import pl.karol.restassured.main.rop.user.DeleteUserEndpoint;
import pl.karol.restassured.main.rop.user.LoginUserEndpoint;
import pl.karol.restassured.main.test.data.user.UserTestDataGenerator;

public class CreateUserTest extends SuiteTestBase {

    private User actualUser;
    private LoginResponse token;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    User user = new UserTestDataGenerator().generateUser();

    @Test
    public void givenUserWhenPostUserThenUserCreatedTest(){

        //Creating user
        actualUser = new CreateUserEndpoint().setUser(user).sendRequest().assertRequestSuccess().getResponseModel();
        //Checking only 4 fields
        Assertions.assertThat(actualUser).describedAs("firstName, lastName, username should be the same!")
                .usingRecursiveComparison().ignoringFields("password", "id").isEqualTo(user);
        //Assertion about password
        Assertions.assertThat(passwordEncoder.matches(user.getPassword(), actualUser.getPassword().substring("{bcrypt}".length()))).isTrue();

    }

    @AfterMethod
    public void cleanUpAfterTest(){
        //Login test endpoint needed for jwt -> jwt needed to user remove (cleaning up after test)
        token = new LoginUserEndpoint().setUsername(user.getUsername()).setPassword(user.getPassword())
                .sendRequest().assertRequestSuccess().getResponseModel();

        //Clean up the user
        new DeleteUserEndpoint().setToken(token.getAccess_token()).sendRequest().assertRequestSuccess();
        //Making sure that user has been deleted, then asserting for 404 status code
        new DeleteUserEndpoint().setToken(token.getAccess_token()).sendRequest().assertStatusCode(HttpStatus.SC_NOT_FOUND);
    }
}
