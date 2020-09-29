package sn.controller;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.multipart.MultipartFile;
import sn.api.response.FileUploadResponse;
import sn.api.response.ServiceResponse;
import sn.service.StorageService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
        MockMultipartFile file = new MockMultipartFile("user-avatar", "myAvatar.png",
                "image", "test data".getBytes());
        Mockito.doReturn(ResponseEntity.status(HttpStatus.OK).body(new ServiceResponse<>(fileUploadResponse)))
                .when(storageService).uploadFile(file, "type");
        mockMvc.perform(multipart("/storage")
                .file(file)
                .queryParam("type", "IMAGE"))
                .andExpect(status().isOk())
                .andReturn();
    }

    /**
     * Если параметр type=null
     * @throws Exception
     */
    @Test
    public void whenTypeParamIsNullThenGetErrorResponse() throws Exception {
        MockMultipartFile file = new MockMultipartFile("user-avatar", "myAvatar.png",
                "image", "test data".getBytes());
        Mockito.doReturn(createErrorResponse("incorrect type", "file type must be \"image\" or \"IMAGE\""))
                .when(storageService).uploadFile(file, Matchers.nullValue().toString());
        mockMvc.perform(multipart("/storage")
                .file(file)
                .queryParam("type", String.valueOf(Matchers.nullValue())))
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
        MockMultipartFile file = new MockMultipartFile("user-avatar", "myAvatar.png",
                "image", "test data".getBytes());
        Mockito.doReturn(createErrorResponse("incorrect type", "file type must be \"image\" or \"IMAGE\""))
                .when(storageService).uploadFile(file, "pdf");
        mockMvc.perform(multipart("/storage")
                .file(file)
                .queryParam("type", "pdf"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("incorrect type")))
                .andReturn();
    }

    /**
     * Если параметр path=null
     * @throws Exception
     */
    @Test
    public void whenFileParamIsNullThenGetErrorResponse() throws Exception {
        Mockito.doReturn(createErrorResponse("incorrect patch", "path is null or empty"))
                .when(storageService).uploadFile(null, String.valueOf(Matchers.nullValue()));
        mockMvc.perform(multipart("/storage")
                .file(null)
                .queryParam("type", "IMAGE"))
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
        MockMultipartFile file = new MockMultipartFile("user-avatar", "myAvatar.png",
                "image", "test data".getBytes());
        Mockito.doReturn(createErrorResponse("IO exception", "some error desc"))
                .when(storageService).uploadFile(file, path);
        mockMvc.perform(multipart("/storage")
                .file(file)
                .queryParam("path", path))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("IO exception")))
                .andReturn();
    }
}
