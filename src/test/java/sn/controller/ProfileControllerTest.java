package sn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import sn.api.response.*;
import sn.model.Person;
import sn.repositories.PersonRepository;
import sn.service.IAccountService;
import sn.service.ICommentService;
import sn.service.IDialogService;
import sn.service.IPostService;
import sn.service.impl.AccountService;
import sn.service.impl.CommentService;
import sn.service.impl.DialogService;
import sn.service.impl.PostService;
import sn.utils.TimeUtil;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
@AutoConfigureMockMvc
@SpringBootTest
public class ProfileControllerTest {
    @Autowired
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

    /**
     * Тест на загрузку контекста.
     */
    @Test
    public void contextLoads() {
        assertThat(profileController).isNotNull();
    }


    /**
     * Получение текущего пользователя.
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

//        Mockito.verify(accountService, Mockito.times(1)).findCurrentUser();
//        Mockito.verify(accountService, Mockito.times(1)).getPersonResponse(person);
//        Mockito.verifyNoInteractions(accountService);
    }
}
