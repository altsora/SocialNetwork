package sn.controller;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import sn.api.response.FileUploadResponse;
import sn.api.response.ServiceResponse;
import sn.service.StorageService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sn.api.response.AbstractResponse.createErrorResponse;

/**
 * Класс StorageControllerTest.
 * MVC тесты для StorageController.
 * @see StorageController
 * @see StorageService
 * @see AbstractWebController
 */
@WithMockUser(username = AbstractWebController.USER_EMAIL)
public class StorageControllerTest extends AbstractWebController {

    @Autowired
    private StorageController storageController;

    @MockBean
    private StorageService storageService;

    @Test
    public void contextLoads() {
        assertThat(storageController).isNotNull();
    }

    /**
     * Если загрузка картинки на cloudinary прошла успешно.
     * @throws Exception
     */
    @Test
    public void whenFileUploadedToCloudinaryThenResponseIfOk() throws Exception {
        FileUploadResponse fileUploadResponse = FileUploadResponse.builder().build();
        Mockito.doReturn(ResponseEntity.status(HttpStatus.OK).body(new ServiceResponse<>(fileUploadResponse)))
                .when(storageService).uploadFile("type", "path");
        mockMvc.perform(post("/storage")
                .queryParam("type", "type")
                .queryParam("path", "path"))
                .andExpect(status().isOk())
                .andReturn();
    }

    /**
     * Если параметр type=null
     * @throws Exception
     */
    @Test
    public void whenTypeParamIsNullThenGetErrorResponse() throws Exception {
        Mockito.doReturn(createErrorResponse("incorrect type", "file type must be \"image\" or \"IMAGE\""))
                .when(storageService).uploadFile(String.valueOf(Matchers.nullValue()), "path");
        mockMvc.perform(post("/storage")
                .queryParam("type", String.valueOf(Matchers.nullValue()))
                .queryParam("path", "path"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("incorrect type")))
                .andReturn();
    }

    /**
     * Если параметр type не равен "IMAGE"
     * @throws Exception
     */
    @Test
    public void whenTypeParamIsNotImageThenGetErrorResponse() throws Exception {
        Mockito.doReturn(createErrorResponse("incorrect type", "file type must be \"image\" or \"IMAGE\""))
                .when(storageService).uploadFile("pdf", "path");
        mockMvc.perform(post("/storage")
                .queryParam("type", "pdf")
                .queryParam("path", "path"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("incorrect type")))
                .andReturn();
    }

    /**
     * Если параметр path=null
     * @throws Exception
     */
    @Test
    public void whenPathParamIsNullThenGetErrorResponse() throws Exception {
        Mockito.doReturn(createErrorResponse("incorrect patch", "path is null or empty"))
                .when(storageService).uploadFile("IMAGE", String.valueOf(Matchers.nullValue()));
        mockMvc.perform(post("/storage")
                .queryParam("type", "IMAGE")
                .queryParam("path", String.valueOf(Matchers.nullValue())))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("incorrect patch")))
                .andReturn();
    }

    /**
     * Если параметр type задан некорректно.
     * @throws Exception
     */
    @Test
    public void whePathIsIncorrectThenGetErrorResponse() throws Exception {
        String path = "-//-a";
        Mockito.doReturn(createErrorResponse("IO exception", "some error desc"))
                .when(storageService).uploadFile("IMAGE", path);
        mockMvc.perform(post("/storage")
                .queryParam("type", "IMAGE")
                .queryParam("path", path))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("IO exception")))
                .andReturn();
    }
}
