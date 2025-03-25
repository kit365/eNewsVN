package com.fpt.enewsvn.test;

import org.springframework.boot.test.context.SpringBootTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@SpringBootTest
public class LoginTest {

    @DataProvider(name = "loginData")
    public Object[][] loginData() {
        return new Object[][]{
                {"user1", "password1", true},
                {"user2", "wrongpassword", false},
                {"", "password3", false},
        };
    }

    @Test(dataProvider = "loginData", groups = "login")
    public void testLogin(String username, String password, boolean expectedResult) {
        boolean actualResult = performLogin(username, password);
        Assert.assertEquals(actualResult, expectedResult, "Login test failed for user: " + username);
    }

    private boolean performLogin(String username, String password) {
        return "user1".equals(username) && "password1".equals(password);
    }
}
