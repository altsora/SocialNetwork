package sn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import sn.api.requests.LikeRequest;
import sn.model.enums.LikeType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Класс LikeControllerTest.
 * MVC тесты для LikeController.
 * @version 1.0
 * @see LikeController
 */


public class LikeControllerTest extends AbstractWebController {

    @Autowired
    private LikeController likeController;

    @Test
    public void contextLoads() {
        assertThat(likeController).isNotNull();
    }

    /**
     * Тетс получения лайков
     */

    @Test
    @WithMockUser(username = USER_EMAIL)
    public void getLikesTest() throws Exception {
        mockMvc.perform(get("/liked")
                .param("user_id", "1")
                .param("item_id", "1")
                .param("type", "POST"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Не верные параметры в запросе
     */

    @Test
    @WithMockUser(username = USER_EMAIL)
    public void getLikesTestWrongParameters() throws Exception {
        mockMvc.perform(get("/liked")
                .param("user_id", "1")
                .param("item_id", "1")
                .param("type", "ABRAKADABRA"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    /**
     * Указанного ползователя нет в БД
     */

    @Test
    @WithMockUser(username = USER_EMAIL)
    public void getLikesNoUserInDB() throws Exception {
        mockMvc.perform(get("/liked")
                .param("user_id", "-11")
                .param("item_id", "1")
                .param("type", "POST"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }


    /**
     * Пользватель не автаризован
     */

    @Test
    public void getLikesTestUnAuthUser() throws Exception {
        mockMvc.perform(get("/liked")
                .param("user_id", "1")
                .param("item_id", "1")
                .param("type", "POST"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andReturn();
    }


    /**
     * Получение списка пользователей
     */

    @Test
    @WithMockUser(username = USER_EMAIL)
    public void getUsersOfLikeTest() throws Exception {
        mockMvc.perform(get("/likes")
                .param("item_id", "3")
                .param("type", "POST"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }


    /**
     * Ставим лайк
     */

    @Test
    @WithMockUser(username = USER_EMAIL)
    public void putLikeTest() throws Exception {
        LikeRequest likeRequest = new LikeRequest(10, LikeType.COMMENT);

        mockMvc.perform(MockMvcRequestBuilders.put("/likes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(likeRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }


    /**
     * Попытка поставить лайк еще раз
     */

    @Test
    @WithMockUser(username = USER_EMAIL)
    public void putLikeAgainTest() throws Exception {
        LikeRequest likeRequest = new LikeRequest(1, LikeType.POST);

        mockMvc.perform(MockMvcRequestBuilders.put("/likes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(likeRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }


    /**
     * Удалить лайк
     */

    @Test
    @WithMockUser(username = USER_EMAIL)
    public void removeLikeTest() throws Exception {
        mockMvc.perform(delete("/likes")
                .param("item_id", "10")
                .param("type", "COMMENT"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }


    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




}
