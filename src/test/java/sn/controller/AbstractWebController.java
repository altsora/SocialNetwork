package sn.controller;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author Andrey.Kazakov
 * @date 10.09.2020
 */
@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class AbstractWebController {
    protected final static String USER_EMAIL = "bro@malta.com";
    protected final static String USER_PASSWORD = "Qwerty0987!";

    @Autowired
    protected MockMvc mockMvc;
}

