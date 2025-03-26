package com.fpt.enewsvn.test;

import com.fpt.enewsvn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@SpringBootTest
public class Testing extends AbstractTestNGSpringContextTests {

    @Autowired
    private UserService userService;

    @DataProvider(name = "loginData")
    public Object[][] loginData() {
        return new Object[][]{
                {"phu@example.com", "1", true},
                {"dung@example.com", "1", true},
                {"kiet@example.com", "1", true},
                {"", "password3", false},
        };
    }

    @Test(dataProvider = "loginData", groups = "login")
    public void testLogin(String email, String password, boolean expectedResult) {
        boolean actualResult = userService.login(email, password);
        Assert.assertEquals(actualResult, expectedResult, "Login test failed for user: " + email);
    }







}
