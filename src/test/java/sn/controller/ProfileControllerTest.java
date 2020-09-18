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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import sn.api.requests.PersonEditRequest;
import sn.api.requests.WallPostRequest;
import sn.api.response.CommentResponse;
import sn.api.response.PersonResponse;
import sn.api.response.WallPostResponse;
import sn.model.Person;
import sn.model.Post;
import sn.repositories.PersonRepository;
import sn.service.AccountService;
import sn.service.CommentService;
import sn.service.PostService;
import sn.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Класс ProfileControllerTest.
 * MVC тесты для ProfileController.
 *
 * @version 1.0
 * @see ProfileController
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProfileControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ProfileController profileController;


    @MockBean
    private CommentService commentService;

    @MockBean
    private PostService postService;

    @MockBean
    private PersonRepository personRepository;

    @MockBean
    private AccountService accountService;


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
        assertThat(profileController).isNotNull();
    }


    /**
     * Получение текущего пользователя. УСПЕШНО
     * 200 - пользователь успешно получен
     */
    @Test
    public void getCurrentUserSuccessfully() throws Exception {
        PersonResponse personResponse = PersonResponse.builder().build();
        Person person = new Person();

        Mockito.doReturn(person).when(accountService).findCurrentUser();
        Mockito.doReturn(personResponse).when(accountService).getPersonResponse(person);

        this.mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Получение текущего пользователя. ОШИБКА
     * 401 - ошибка авторизации.
     */
    @Test
    public void getCurrentUserUnsuccessfully() throws Exception {
        Mockito.doReturn(null).when(accountService).findCurrentUser();

        this.mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("User is not authorized")))
                .andReturn();
    }

    /**
     * Редактирование текущего пользователя. УСПЕШНО
     * <p>
     * personEditRequest - тело запроса в формате JSON. Содержит данные новые данные пользователя.
     * 200 - пользователь успешно получен
     */
    @Test
    public void editCurrentUserSuccessfully() throws Exception {
        PersonEditRequest personEditRequest = new PersonEditRequest();
        Person person = new Person();

        Mockito.doReturn(person).when(accountService).findCurrentUser();
        Mockito.doReturn(person).when(accountService).updatePerson(person, personEditRequest);

        this.mockMvc.perform(put("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(personEditRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Редактирование текущего пользователя. ОШИБКА
     * <p>
     * personEditRequest - тело запроса в формате JSON. Содержит данные новые данные пользователя.
     * 401 - ошибка авторизации.
     */
    @Test
    public void editCurrentUserUnsuccessfully() throws Exception {
        PersonEditRequest personEditRequest = new PersonEditRequest();
        Mockito.doReturn(null).when(accountService).findCurrentUser();

        this.mockMvc.perform(put("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(personEditRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("User is not authorized")))
                .andReturn();
    }


    /**
     * Удаление текущего пользователя. УСПЕШНО
     * 200 - пользователь удалён
     */
    @Test
    public void deleteCurrentUserSuccessfully() throws Exception {
        Person person = new Person();

        Mockito.doReturn(person).when(accountService).findCurrentUser();
        Mockito.doNothing().when(personRepository).deleteById(person.getId());

        this.mockMvc.perform(delete("/users/me"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("ok")))
                .andReturn();
    }

    /**
     * Удаление текущего пользователя. ОШИБКА
     * 401 - ошибка авторизации.
     */
    @Test
    public void deleteCurrentUserUnsuccessfully() throws Exception {
        Mockito.doReturn(null).when(accountService).findCurrentUser();

        this.mockMvc.perform(delete("/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("User is not authorized")))
                .andReturn();
    }


    /**
     * Получить пользователя по id. УСПЕШНО
     * personId - ID пользователя, которого надо получить.
     * 200 - пользователь удалён
     */
    @Test
    public void getUserByIdSuccessfully() throws Exception {
        long id = 0;
        Person person = new Person();
        person.setId(id);
        PersonResponse personResponse = PersonResponse.builder().id(id).build();

        Mockito.doReturn(Optional.of(person)).when(personRepository).findById(person.getId());
        Mockito.doReturn(personResponse).when(accountService).getPersonResponse(person);

        this.mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Получить пользователя по id. ОШИБКА
     * personId - ID пользователя, которого надо получить.
     * 400 - произошла ошибка
     */
    @Test
    public void getUserByIdUnsuccessfully() throws Exception {
        long id = 0;

        Mockito.doReturn(Optional.empty()).when(personRepository).findById(id);

        this.mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Service unavailable")))
                .andReturn();
    }

    /**
     * Получение записей на стене пользователя. УСПЕШНО
     * personId    - ID пользователя, со стены которого требуется получить записи.
     * offset      - Отступ от начала результирующего списка публикаций.
     * itemPerPage - Количество публикаций из результирующего списка, которые представлены для отображения.
     * 200 - получение результирующего списка с публикациями на стене пользователя;
     */
    @Test
    public void getWallPostsSuccessfully() throws Exception {
        long id = 0;
        int offset = 0;
        int itemPerPage = 20;


        Person person = new Person();
        person.setId(id);
        PersonResponse personResponse = PersonResponse.builder().id(id).build();

        int totalPostCount = 1;
        Post post = new Post();
        List<Post> posts = new ArrayList<>();
        posts.add(post);


        WallPostResponse wallPostResponse = WallPostResponse.builder().build();
        List<CommentResponse> comments = new ArrayList<>();

        Mockito.doReturn(Optional.of(person)).when(personRepository).findById(person.getId());
        Mockito.doReturn(posts).when(postService).findAllByPersonId(id, offset, itemPerPage);
        Mockito.doReturn(personResponse).when(accountService).getPersonResponse(person);
        Mockito.doReturn(comments).when(commentService).getCommentsByPostId(id);
        Mockito.doReturn(wallPostResponse).when(postService).getExistsWallPost(post, personResponse, comments);
        Mockito.doReturn(totalPostCount).when(postService).getTotalCountPostsByPersonId(id);

        this.mockMvc.perform(get("/users/{id}/wall", id)
                .param("offset", String.valueOf(offset))
                .param("itemPerPage", String.valueOf(itemPerPage)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Получение записей на стене пользователя. ОШИБКА 400
     * personId    - ID пользователя, со стены которого требуется получить записи.
     * offset      - Отступ от начала результирующего списка публикаций.
     * itemPerPage - Количество публикаций из результирующего списка, которые представлены для отображения.
     * 400 - произошла ошибка;
     */
    @Test
    public void getWallPostsUnsuccessfully() throws Exception {
        long id = 0;
        int offset = 0;
        int itemPerPage = 20;

        Mockito.doReturn(Optional.empty()).when(personRepository).findById(id);


        this.mockMvc.perform(get("/users/{id}/wall", id)
                .param("offset", String.valueOf(offset))
                .param("itemPerPage", String.valueOf(itemPerPage)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Service unavailable")))
                .andReturn();
    }

    /**
     * Добавление публикации на стену пользователя. УСПЕШНО
     * personId        - ID пользователя, который публикует записи.
     * publishDate     - Дата публикации, установленная пользователем.
     * wallPostRequest - тело запроса в формате JSON. Содержит данные о новой публикации.
     * 200 - запись готова к публикации к назначенному времени;
     */
    @Test
    public void addWallPostSuccessfully() throws Exception {
        long id = 0;
        long publishDate = 1590217200;
        String title = "title";
        String text = "text";

        WallPostRequest wallPostRequest = new WallPostRequest();
        wallPostRequest.setPostText(text);
        wallPostRequest.setTitle(title);
        Post post = new Post();

        Person person = new Person();
        person.setId(id);

        PersonResponse personResponse = PersonResponse.builder().id(id).build();
        WallPostResponse wallPostResponse = WallPostResponse.builder().build();

        Mockito.doReturn(Optional.of(person)).when(personRepository).findById(person.getId());
        Mockito.doReturn(post).when(postService).addPost(person, title, text, TimeUtil.getLocalDateTimeFromTimestamp(publishDate));
        Mockito.doReturn(personResponse).when(accountService).getPersonResponse(person);
        Mockito.doReturn(wallPostResponse).when(postService).createNewWallPost(post, personResponse);

        this.mockMvc.perform(post("/users/{id}/wall", id)
                .param("publish_date", String.valueOf(publishDate))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(wallPostRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Добавление публикации на стену пользователя. ОШИБКА 400
     * personId        - ID пользователя, который публикует записи.
     * publishDate     - Дата публикации, установленная пользователем.
     * wallPostRequest - тело запроса в формате JSON. Содержит данные о новой публикации.
     * 400 - произошла ошибка;
     */
    @Test
    public void addWallPostUnsuccessfully() throws Exception {
        long id = 0;
        long publishDate = 1590217200;
        String title = "title";
        String text = "text";

        WallPostRequest wallPostRequest = new WallPostRequest();
        wallPostRequest.setPostText(text);
        wallPostRequest.setTitle(title);

        Mockito.doReturn(Optional.empty()).when(personRepository).findById(id);


        this.mockMvc.perform(post("/users/{id}/wall", id)
                .param("publish_date", String.valueOf(publishDate))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(wallPostRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Service unavailable")))
                .andReturn();
    }


    /**
     * Поиск пользователей по указанным параметрам. УСПЕШНО
     * firstName   - Имя пользователей.
     * lastName    - Фамилия пользователей.
     * ageFrom     - Минимальный возраст пользователей.
     * ageTo       - Максимальный возраст пользователей.
     * countryId   - Идентификатор страны пользователей.
     * cityId      - Идентификатор города пользователей.
     * offset      - Отступ от начала результирующего списка пользователей.
     * itemPerPage - Количество пользователей из результирующего списка, которые представлены для отображения.
     * 200 - Возврат списка пользователей, подходящих по указанным параметрам;
     */
    @Test
    public void findUserSuccessfully() throws Exception {
        String firstName = "first_name";
        String lastName = "last_name";
        Integer ageFrom = 1;
        Integer ageTo = 1;
        Integer countryId = 1;
        Integer cityId = 1;
        Integer offset = 0;
        Integer itemPerPage = 20;

        int totalCountUser = 1;
        Person person = new Person();
        PersonResponse personResponse = PersonResponse.builder().build();

        List<Person> personList = new ArrayList<>();
        personList.add(person);

        //TODO: без учёта города и страны
        Mockito.doReturn(personList).when(accountService).searchPersons(firstName, lastName, ageFrom, ageTo, offset, itemPerPage);
        Mockito.doReturn(personResponse).when(accountService).getPersonResponse(person);
        Mockito.doReturn(totalCountUser).when(personRepository).getTotalCountUsers();

        this.mockMvc.perform(get("/users/search")
                .param("first_name", firstName)
                .param("last_name", lastName)
                .param("age_from", String.valueOf(ageFrom))
                .param("age_to", String.valueOf(ageTo))
                .param("offset", String.valueOf(offset))
                .param("itemPerPage", String.valueOf(itemPerPage)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total", is(totalCountUser)))
                .andExpect(jsonPath("$.offset", is(offset)))
                .andExpect(jsonPath("$.perPage", is(itemPerPage)))
                .andExpect(jsonPath("$.data").isArray())
                .andReturn();
    }

    /**
     * Поиск пользователей по указанным параметрам. ОШИБКА 400
     * firstName   - Имя пользователей.
     * lastName    - Фамилия пользователей.
     * ageFrom     - Минимальный возраст пользователей.
     * ageTo       - Максимальный возраст пользователей.
     * countryId   - Идентификатор страны пользователей.
     * cityId      - Идентификатор города пользователей.
     * offset      - Отступ от начала результирующего списка пользователей.
     * itemPerPage - Количество пользователей из результирующего списка, которые представлены для отображения.
     * 400 - произошла ошибка
     */
    @Test
    public void findUserUnsuccessfully() throws Exception {
        String firstName = "first_name";
        String lastName = "last_name";
        Integer ageFrom = 1;
        Integer ageTo = 1;
        Integer countryId = 1;
        Integer cityId = 1;
        Integer offset = 0;
        Integer itemPerPage = 20;


        //TODO: сделать тест для 400 - произошла ошибка В методе нет


        this.mockMvc.perform(get("/users/search")
                .param("first_name", firstName)
                .param("last_name", lastName)
                .param("age_from", String.valueOf(ageFrom))
                .param("age_to", String.valueOf(ageTo))
                .param("offset", String.valueOf(offset))
                .param("itemPerPage", String.valueOf(itemPerPage)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andReturn();
    }

    /**
     * Блокировка пользователя. УСПЕШНО 200
     * personId - ID пользователя, которого надо заблокировать.
     * 200 - пользователь заблокирован
     */
    @Test
    public void blockUserByIdSuccessfully() throws Exception {
        long id = 0;


        Mockito.doReturn(true).when(accountService).changeUserLockStatus(id);

        this.mockMvc.perform(put("/users/block/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("ok")))
                .andReturn();
    }

    /**
     * Блокировка пользователя. Ошибка 400
     * personId - ID пользователя, которого надо заблокировать.
     * 400 - произошла ошибка
     */
    @Test
    public void blockUserByIdUnsuccessfully() throws Exception {
        long id = 0;

        Mockito.doReturn(false).when(accountService).changeUserLockStatus(id);

        this.mockMvc.perform(put("/users/block/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Service unavailable")))
                .andReturn();
    }

    /**
     * Разблокировка пользователя. УСПЕШНО 200
     * personId - ID пользователя, которого надо заблокировать.
     * 200 - пользователь заблокирован
     */
    @Test
    public void unblockUserByIdSuccessfully() throws Exception {
        long id = 0;


        Mockito.doReturn(true).when(accountService).changeUserLockStatus(id);

        this.mockMvc.perform(delete("/users/block/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("ok")))
                .andReturn();
    }

    /**
     * Разблокировка пользователя. Ошибка 400
     * personId - ID пользователя, которого надо заблокировать.
     * 400 - произошла ошибка
     */
    @Test
    public void unblockUserByIdUnsuccessfully() throws Exception {
        long id = 0;

        Mockito.doReturn(false).when(accountService).changeUserLockStatus(id);

        this.mockMvc.perform(delete("/users/block/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Service unavailable")))
                .andReturn();
    }
}


