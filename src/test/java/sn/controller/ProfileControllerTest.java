package sn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import sn.api.requests.PersonEditRequest;
import sn.api.requests.WallPostRequest;
import sn.api.response.AbstractResponse;
import sn.api.response.ServiceResponse;
import sn.api.response.ServiceResponseDataList;
import sn.service.AccountService;
import sn.utils.ErrorUtil;

import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Класс ProfileControllerTest.
 * MVC тесты для ProfileController.
 *
 * @version 1.0
 * @see ProfileController
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProfileControllerTest extends AbstractWebController {
    @Autowired
    private ProfileController profileController;

    @MockBean
    private AccountService accountService;

    private static ResponseEntity<ServiceResponse<AbstractResponse>> okResponse = ResponseEntity.ok(new ServiceResponse<>());
    private static ResponseEntity<ServiceResponse<AbstractResponse>> badRequestResponse = ErrorUtil.badRequest("bad request");
    private static ResponseEntity<ServiceResponse<AbstractResponse>> unauthorizedResponse = ErrorUtil.unauthorized();


    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(profileController).build();
    }

    /**
     * Тест на загрузку контекста.
     */
    @Test
    public void contextLoads() {
        assertNotNull(profileController);
    }

    //==================================================================================================================

    /**
     * Получение текущего пользователя. УСПЕШНО.
     */
    @Test
    public void getCurrentUserOk() throws Exception {
        Mockito.doReturn(okResponse)
                .when(accountService)
                .getCurrentUser();

        mockMvc.perform(get("/users/me"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Получение текущего пользователя. Пользователь не авторизован.
     */
    @Test
    public void getCurrentUserUnauthorized() throws Exception {
        Mockito.doReturn(unauthorizedResponse)
                .when(accountService)
                .getCurrentUser();

        mockMvc.perform(get("/users/me"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Редактирование текущего пользователя. УСПЕШНО.
     */
    @Test
    public void editCurrentUserOk() throws Exception {
        PersonEditRequest personEditRequest = new PersonEditRequest();

        Mockito.doReturn(okResponse)
                .when(accountService)
                .editUser(personEditRequest);

        mockMvc.perform(put("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(personEditRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Редактирование текущего пользователя. Пользователь не авторизован.
     */
    @Test
    public void editCurrentUserUnauthorized() throws Exception {
        PersonEditRequest personEditRequest = new PersonEditRequest();

        Mockito.doReturn(unauthorizedResponse)
                .when(accountService)
                .editUser(personEditRequest);

        mockMvc.perform(put("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(personEditRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Удаление текущего пользователя. УСПЕШНО.
     */
    @Test
    public void deleteCurrentUserOk() throws Exception {
        Mockito.doReturn(okResponse)
                .when(accountService)
                .deleteUser();

        mockMvc.perform(delete("/users/me"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Удаление текущего пользователя. Пользователь не авторизован.
     */
    @Test
    public void deleteCurrentUserUnauthorized() throws Exception {
        Mockito.doReturn(unauthorizedResponse)
                .when(accountService)
                .deleteUser();

        mockMvc.perform(delete("/users/me"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Получить пользователя по id. УСПЕШНО.
     */
    @Test
    public void getUserByIdOk() throws Exception {
        long personId = 1;

        Mockito.doReturn(okResponse)
                .when(accountService)
                .getUserById(personId);

        mockMvc.perform(get("/users/{id}", personId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Получить пользователя по id. Bad request: пользователь не найден.
     */
    @Test
    public void getUserByIdBadRequest() throws Exception {
        long personId = 1;

        Mockito.doReturn(badRequestResponse)
                .when(accountService)
                .getUserById(personId);

        mockMvc.perform(get("/users/{id}", personId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Получение записей на стене пользователя. УСПЕШНО.
     */
    @Test
    public void getWallPostsOk() throws Exception {
        long personId = 1;
        int offset = 0;
        int itemPerPage = 20;
        int total = 100;

        Mockito.doReturn(ResponseEntity
                .ok(new ServiceResponseDataList<>(total, offset, itemPerPage, new ArrayList<>())))
                .when(accountService)
                .getWallPosts(personId, offset, itemPerPage);

        mockMvc.perform(get("/users/{id}/wall", personId)
                .param("offset", String.valueOf(offset))
                .param("itemPerPage", String.valueOf(itemPerPage)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Получение записей на стене пользователя. BadRequest: пользователь не найден.
     */
    @Test
    public void getWallPostsBadRequest() throws Exception {
        long personId = 1;
        int offset = 0;
        int itemPerPage = 20;

        Mockito.doReturn(ResponseEntity
                .badRequest()
                .body(new ServiceResponseDataList<>("User with ID = " + personId + " not found")))
                .when(accountService)
                .getWallPosts(personId, offset, itemPerPage);

        mockMvc.perform(get("/users/{id}/wall", personId)
                .param("offset", String.valueOf(offset))
                .param("itemPerPage", String.valueOf(itemPerPage)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Добавление публикации на стену пользователя. УСПЕШНО.
     */
    @Test
    public void addWallPostOk() throws Exception {
        long personId = 1;
        Long publishDate = 2L;
        WallPostRequest wallPostRequest = new WallPostRequest();

        Mockito.doReturn(okResponse)
                .when(accountService)
                .addWallPost(personId, publishDate, wallPostRequest);

        mockMvc.perform(post("/users/{id}/wall", personId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(wallPostRequest))
                .param("publish_date", String.valueOf(publishDate)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Добавление публикации на стену пользователя. УСПЕШНО.
     */
    @Test
    public void addWallPostPublishDateIsNullOk() throws Exception {
        long personId = 1;
        Long publishDate = null;
        WallPostRequest wallPostRequest = new WallPostRequest();

        Mockito.doReturn(okResponse)
                .when(accountService)
                .addWallPost(personId, publishDate, wallPostRequest);

        mockMvc.perform(post("/users/{id}/wall", personId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(wallPostRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Добавление публикации на стену пользователя. BadRequest: пользователь не найден.
     */
    @Test
    public void addWallPostBadRequest() throws Exception {
        long personId = 1;
        Long publishDate = 2L;
        WallPostRequest wallPostRequest = new WallPostRequest();

        Mockito.doReturn(badRequestResponse)
                .when(accountService)
                .addWallPost(personId, publishDate, wallPostRequest);

        mockMvc.perform(post("/users/{id}/wall", personId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(wallPostRequest))
                .param("publish_date", String.valueOf(publishDate)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Поиск пользователей по указанным параметрам. Указаны все параметры. УСПЕШНО.
     */
    @Test
    public void findUsersOk() throws Exception {
        String firstName = "first_name";
        String lastName = "last_name";
        String city = "Moscow";
        String country = "Russia";
        Integer ageFrom = 1;
        Integer ageTo = 2;
        Integer offset = 0;
        Integer itemPerPage = 20;

        Mockito.doReturn(okResponse)
                .when(accountService)
                .findUsers(firstName, lastName, city, country, ageFrom, ageTo, offset, itemPerPage);

        mockMvc.perform(get("/users/search")
                .param("first_name", firstName)
                .param("last_name", lastName)
                .param("city", city)
                .param("country", country)
                .param("age_from", String.valueOf(ageFrom))
                .param("age_to", String.valueOf(ageTo))
                .param("offset", String.valueOf(offset))
                .param("itemPerPage", String.valueOf(itemPerPage)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Поиск пользователей по указанным параметрам. Не указаны ID страны и города. УСПЕШНО.
     */
    @Test
    public void findUsersWithoutCountryIdAndCityIdOk() throws Exception {
        String firstName = "first_name";
        String lastName = "last_name";
        String city = "Moscow";
        String country = "Russia";
        Integer ageFrom = 1;
        Integer ageTo = 2;
        Integer offset = 0;
        Integer itemPerPage = 20;

        Mockito.doReturn(okResponse)
                .when(accountService)
                .findUsers(firstName, lastName, city, country, ageFrom,  ageTo, offset, itemPerPage);

        mockMvc.perform(get("/users/search")
                .param("first_name", firstName)
                .param("last_name", lastName)
                .param("city", city)
                .param("country", country)
                .param("age_from", String.valueOf(ageFrom))
                .param("age_to", String.valueOf(ageTo))
                .param("offset", String.valueOf(offset))
                .param("itemPerPage", String.valueOf(itemPerPage)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Поиск пользователей по указанным параметрам. Параметры поиска не указаны. УСПЕШНО.
     */
    @Test
    public void findUsersNoParametersOk() throws Exception {
        String firstName = null;
        String lastName = null;
        String city = null;
        String country = null;
        Integer ageFrom = null;
        Integer ageTo = null;
        Integer offset = 0;
        Integer itemPerPage = 20;

        Mockito.doReturn(okResponse)
                .when(accountService)
                .findUsers(firstName, lastName, city, country, ageFrom, ageTo, offset, itemPerPage);

        mockMvc.perform(get("/users/search")
                .param("offset", String.valueOf(offset))
                .param("itemPerPage", String.valueOf(itemPerPage)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Блокировка пользователя. УСПЕШНО.
     */
    @Test
    public void blockUserByIdOk() throws Exception {
        long personId = 1;

        Mockito.doReturn(okResponse)
                .when(accountService)
                .changeUserLockStatus(personId);

        mockMvc.perform(put("/users/block/{id}", personId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Блокировка пользователя. Bad request: пользователь не найден.
     */
    @Test
    public void blockUserByIdBadRequest() throws Exception {
        long personId = 1;

        Mockito.doReturn(badRequestResponse)
                .when(accountService)
                .changeUserLockStatus(personId);

        mockMvc.perform(put("/users/block/{id}", personId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Разблокировка пользователя. УСПЕШНО.
     */
    @Test
    public void unblockUserByIdOk() throws Exception {
        long personId = 1;

        Mockito.doReturn(okResponse)
                .when(accountService)
                .changeUserLockStatus(personId);

        mockMvc.perform(delete("/users/block/{id}", personId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Разблокировка пользователя. Bad request: пользователь не найден.
     */
    @Test
    public void unblockUserByIdBadRequest() throws Exception {
        long personId = 1;

        Mockito.doReturn(badRequestResponse)
                .when(accountService)
                .changeUserLockStatus(personId);

        mockMvc.perform(delete("/users/block/{id}", personId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }
}


