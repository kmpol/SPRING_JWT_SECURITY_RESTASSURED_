package pl.karol.jwt_based_app.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.assertj.core.api.Assertions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pl.karol.restassured.main.pojo.LoginResponse;
import pl.karol.restassured.main.pojo.User;
import pl.karol.restassured.main.rop.user.CreateUserEndpoint;
import pl.karol.restassured.main.rop.user.DeleteUserEndpoint;
import pl.karol.restassured.main.rop.user.LoginUserEndpoint;
import pl.karol.restassured.main.test.data.user.UserTestDataGenerator;

public class LoginUserTest {

    User user = new UserTestDataGenerator().generateUser();
    LoginResponse tokensResponse;

    @Test
    public void givenUserWhenLoginThenUserGetsJwtTest(){
        new CreateUserEndpoint().setUser(user).sendRequest().assertRequestSuccess();
        tokensResponse = new LoginUserEndpoint().setUsername(user.getUsername()).setPassword(user.getPassword())
                .sendRequest().assertRequestSuccess().getResponseModel();

        DecodedJWT decode = JWT.decode(tokensResponse.getAccess_token());
        String subject = decode.getSubject();
        Assertions.assertThat(subject).isEqualTo(user.getUsername());
    }

    @AfterMethod
    public void cleanUpAfterTest(){
        new DeleteUserEndpoint().setToken(tokensResponse.getAccess_token()).sendRequest().assertRequestSuccess();
    }
}
