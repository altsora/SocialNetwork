package sn.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import sn.service.FeedService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Класс FeedControllerTest.
 * MVC тесты для FeedController.
 * @version 1.0
 * @see FeedController
 */

public class FeedControllerTest extends AbstractWebController {

    @Autowired
    private FeedController feedController;

    @MockBean
    private FeedService feedService;

    @Test
    public void contextLoads() {
        assertThat(feedController).isNotNull();
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    public void whenGetFeedsThenReturnResponseWithFeeds() throws Exception {
        this.mockMvc.perform(get("/feeds")
                .param("name", "test query")
                .param("offset", String.valueOf(0))
                .param("itemPerPage", String.valueOf(0)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
}
