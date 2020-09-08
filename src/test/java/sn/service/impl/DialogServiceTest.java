package sn.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import sn.api.response.UserActivityResponse;
import sn.model.Dialog;
import sn.model.Person;
import sn.model.Person2Dialog;
import sn.repositories.DialogRepository;
import sn.repositories.Person2DialogRepository;
import sn.service.IDialogService;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Модульные тесты для DialogService.
 *
 * @see DialogService;
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DialogServiceTest {
    @MockBean
    private DialogRepository dialogRepository;
    @MockBean
    private Person2DialogRepository person2DialogRepository;
    @Autowired
    private IDialogService dialogService;

    private static long dialogId = 1;

    //==================================================================================================================

    /**
     * Поиск диалога в базе. Представлены оба случая
     */
    @Test
    public void findById() {
        Dialog dialog = new Dialog();
        dialog.setId(dialogId);
        Mockito.when(dialogRepository.findById(dialogId)).thenReturn(Optional.of(dialog));
        Dialog actual1 = dialogService.findById(dialogId);

        Mockito.when(dialogRepository.findById(dialogId)).thenReturn(Optional.empty());
        Dialog actual2 = dialogService.findById(dialogId);

        Assert.assertNotNull(actual1);
        Assert.assertNull(actual2);

        Mockito.verify(dialogRepository, Mockito.times(2)).findById(dialogId);
    }

    /**
     * Проверка, существует ли диалог. Представлены оба случая
     */
    @Test
    public void exists() {
        Mockito.when(dialogRepository.existsById(dialogId)).thenReturn(true);
        boolean exists = dialogService.exists(dialogId);

        Mockito.when(dialogRepository.existsById(dialogId)).thenReturn(false);
        boolean notExists = dialogService.exists(dialogId);

        Assert.assertTrue(exists);
        Assert.assertFalse(notExists);

        Mockito.verify(dialogRepository, Mockito.times(2)).existsById(dialogId);
    }

    /**
     * Проверка, существует ли пользователь в диалоге. Представлены оба случая
     */
    @Test
    public void userExistsInDialog() {
        long personId = 2;
        Person person = new Person();
        person.setId(personId);

        Person2Dialog person2Dialog = new Person2Dialog();
        person2Dialog.setId(3);
        person2Dialog.setDialog(Mockito.mock(Dialog.class));
        person2Dialog.setPerson(person);

        Mockito.when(person2DialogRepository.find(personId, dialogId)).thenReturn(person2Dialog);
        boolean exists = dialogService.userExistsInDialog(personId, dialogId);

        Mockito.when(person2DialogRepository.find(personId, dialogId)).thenReturn(null);
        boolean notExists = dialogService.userExistsInDialog(personId, dialogId);

        Assert.assertTrue(exists);
        Assert.assertFalse(notExists);

        Mockito.verify(person2DialogRepository, Mockito.times(2)).find(personId, dialogId);
    }


    /**
     * Уменьшаем количество непрочитанных сообщений в диалоге
     */
    @Test
    public void decreaseUnreadCount() {
        int currentUnreadCount = 5;
        int expectedUnreadCount = currentUnreadCount - 1;

        Dialog dialog = new Dialog();
        dialog.setId(dialogId);
        dialog.setUnreadCount(currentUnreadCount);

        Dialog updateDialog = new Dialog();
        updateDialog.setId(dialog.getId());
        updateDialog.setUnreadCount(expectedUnreadCount);

        Mockito.when(dialogRepository.findById(dialogId)).thenReturn(Optional.of(dialog));
        Mockito.when(dialogRepository.saveAndFlush(dialog)).thenReturn(updateDialog);

        dialogService.decreaseUnreadCount(dialogId);
        Assert.assertEquals(expectedUnreadCount, updateDialog.getUnreadCount());

        Mockito.verify(dialogRepository, Mockito.times(1)).findById(dialogId);
    }

    /**
     * Получаем последнюю активность пользователя
     */
    @Test
    public void getActivity() {
        long personId = 2;
        Person person = new Person();
        person.setId(personId);
        person.setLastOnlineTime(LocalDateTime.now());
        person.setOnline(true);

        Person2Dialog person2Dialog = new Person2Dialog();
        person2Dialog.setId(3);
        person2Dialog.setDialog(Mockito.mock(Dialog.class));
        person2Dialog.setPerson(person);

        Mockito.when(person2DialogRepository.find(personId, dialogId)).thenReturn(person2Dialog);

        UserActivityResponse userActivityResponse = dialogService.getActivity(personId, dialogId);
        Assert.assertNotNull(userActivityResponse);
        Assert.assertNotNull(userActivityResponse.getLastActivity());
        Assert.assertTrue(userActivityResponse.isOnline());

        Mockito.verify(person2DialogRepository, Mockito.times(1)).find(personId, dialogId);
    }
}