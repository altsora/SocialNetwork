package sn.controller;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Andrey.Kazakov
 * @date 21.09.2020
 */
public class NotificationControllerTest extends AbstractWebController {

    @Test
    @WithMockUser(username = USER_EMAIL)
    public void getNotificationListTest() throws Exception {
        this.mockMvc.perform(get("/notifications")
                        .param("offset", String.valueOf(0))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("\"total\":0")))
                .andReturn();
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    public void getNotificationAllTest() throws Exception {
        this.mockMvc.perform(put("/notifications")
                        .param("id", String.valueOf(1))
                        .param("all", String.valueOf(true))
                )
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("\"total\":1")))
                .andReturn();
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    public void getNotificationByIdTest() throws Exception {
        this.mockMvc.perform(put("/notifications")
                        .param("id", String.valueOf(1))
                        .param("all", String.valueOf(false))
                )
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("error")))
                .andReturn();
    }
}
