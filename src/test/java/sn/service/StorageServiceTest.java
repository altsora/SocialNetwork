package sn.service;

import com.cloudinary.Cloudinary;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import sn.api.response.AbstractResponse;
import sn.api.response.FileUploadResponse;
import sn.model.Person;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static sn.api.response.AbstractResponse.createErrorResponse;

/**
 * Класс StorageServiceTest.
 * Unit тесты для StorageService.
 *
 * @see StorageService
 * @see sn.config.CloudinaryConfig
 * @see AccountService
 */
@WithMockUser(username = AbstractServiceTest.USER_EMAIL)
public class StorageServiceTest extends AbstractServiceTest {

    @Autowired
    private StorageService storageService;

    @Autowired
    private Cloudinary cloudinary;

    @MockBean
    private AccountService accountService;

    /**
     * type=null
     */
//    @Test
//    public void whenTypeIsNullThenReturnErrorResponse() {
//        AbstractResponse expected = createErrorResponse(
//                "incorrect type", "file type must be \"image\" or \"IMAGE\"").getBody().getData();
//        AbstractResponse actual = storageService.uploadFile(String.valueOf(Matchers.nullValue()),
//                String.valueOf(Matchers.notNullValue())).getBody().getData();
//        assertThat(actual, samePropertyValuesAs(expected));
//    }

    /**
     * некорректный тип type
     */
//    @Test
//    public void whenTypeIsIncorrectThenReturnErrorResponse() {
//        AbstractResponse expected = createErrorResponse(
//                "incorrect type", "file type must be \"image\" or \"IMAGE\"").getBody().getData();
//        AbstractResponse actual = storageService.uploadFile("incorrect",
//                String.valueOf(Matchers.notNullValue())).getBody().getData();
//        assertThat(actual, samePropertyValuesAs(expected));
//    }

    /**
     * path = null
     */
//    @Test
//    public void whenPathIsNullThenReturnErrorResponse() {
//        AbstractResponse expected = createErrorResponse(
//                "IO exception", "File not found or unreadable: null").getBody().getData();
//        AbstractResponse actual = storageService.uploadFile(
//                "IMAGE", String.valueOf(Matchers.nullValue())).getBody().getData();
//        assertThat(actual, samePropertyValuesAs(expected));
//    }

    /**
     * некорректный path
     */
//    @Test
//    public void whenPathIsIncorrectThenReturnErrorResponse() {
//        String incorrectPath = "=/*w92";
//        AbstractResponse expected = createErrorResponse(
//                "IO exception", "File not found or unreadable: " + incorrectPath).getBody().getData();
//        AbstractResponse actual = storageService.uploadFile("IMAGE", incorrectPath).getBody().getData();
//        assertThat(actual, samePropertyValuesAs(expected));
//    }

    /**
     * проверка ответа cloudinary
     */
//    @Test
//    public void whenPathAndTypeAreCorrectThenUploadImageSuccess() {
//        Person activeUser = new Person();
//        activeUser.setId(10);
//        Mockito.doReturn(activeUser).when(accountService).findCurrentUser();
//        FileUploadResponse expected = FileUploadResponse.builder()
//                .ownerId(activeUser.getId())
//                .fileName("skillbox")
//                .fileFormat("svg")
//                .bytes(2440)
//                .fileType("IMAGE")
//                .build();
//        FileUploadResponse actual = (FileUploadResponse) storageService.uploadFile("IMAGE",
//                "https://248006.selcdn.ru/MainSite/skillbox.svg").getBody().getData();
//        assertEquals(actual.getFileType(), expected.getFileType());
//        assertEquals(actual.getFileName(), expected.getFileName());
//        assertEquals(actual.getFileFormat(), expected.getFileFormat());
//        assertEquals(actual.getBytes(), expected.getBytes());
//        assertEquals(actual.getOwnerId(), expected.getOwnerId());
//    }
}
