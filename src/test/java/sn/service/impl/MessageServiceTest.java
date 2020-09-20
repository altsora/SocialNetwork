package sn.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import sn.api.response.MessageFullResponse;
import sn.model.Dialog;
import sn.model.Message;
import sn.model.Person;
import sn.model.enums.MessageStatus;
import sn.repositories.DialogRepository;
import sn.repositories.MessageRepository;
import sn.service.MessageService;
import sn.utils.TimeUtil;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

/**
 * Модульные тесты для MessageService.
 *
 * @see MessageService ;
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageServiceTest {

    @Autowired
    private MessageService messageService;
    @MockBean
    private MessageRepository messageRepository;
    @MockBean
    private DialogRepository dialogRepository;

    private static long messageId = 1;

    //==================================================================================================================

    /**
     * Поиск сообщения. Сообщение найдено в БД.
     */
    @Test
    public void findById() {
        Message message = new Message();
        Mockito.doReturn(Optional.of(message)).when(messageRepository).findById(messageId);

        Message messageInDb = messageService.findById(messageId);

        Assert.assertSame(messageInDb, message);
        Mockito.verify(messageRepository, Mockito.times(1)).findById(messageId);
        System.out.println(message);
    }

    /**
     * Проверка, существует ли сообщение в базе.
     */
    @Test
    public void existsMessage() {
        Mockito.when(messageRepository.existsById(messageId)).thenReturn(true);
        boolean messageExistsInDb = messageService.exists(messageId);

        Mockito.when(messageRepository.existsById(messageId)).thenReturn(false);
        boolean messageNotExistsInDb = messageService.exists(messageId);

        Assert.assertTrue(messageExistsInDb);
        Assert.assertFalse(messageNotExistsInDb);
        Mockito.verify(messageRepository, Mockito.times(2)).existsById(messageId);
    }

    /**
     * Удаление сообщения и получение его айдишника после удаления.
     */
    @Test
    public void removeMessage() {
        Message message = new Message();
        message.setId(messageId);
        Mockito.when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        Mockito.when(messageRepository.saveAndFlush(message)).thenReturn(message);

        long expectedMessageId = messageService.removeMessage(messageId);
        Assert.assertEquals(messageId, expectedMessageId);

        Mockito.verify(messageRepository, Mockito.times(1)).findById(messageId);
        Mockito.verify(messageRepository, Mockito.times(1)).saveAndFlush(message);
    }

    @Test
    public void recoverMessage() {
        Message message = new Message();
        message.setId(messageId);
        message.setAuthor(Mockito.mock(Person.class));
        message.setStatus(MessageStatus.READ);

        Mockito.when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        Mockito.when(messageRepository.saveAndFlush(message)).thenReturn(message);

        MessageFullResponse messageFullResponse = messageService.recoverMessage(messageId);
        Assert.assertNotNull(messageFullResponse);

        Mockito.verify(messageRepository, Mockito.times(1)).findById(messageId);
        Mockito.verify(messageRepository, Mockito.times(1)).saveAndFlush(message);
    }

    /**
     * Редактирование сообщения
     */
    @Test
    public void editMessage() {
        Person author = new Person();
        author.setId(5);
        Person recipient = new Person();
        recipient.setId(6);

        String oldText = "old text";
        String newText = "new text";
        MessageStatus status = MessageStatus.SENT;

        Message message = new Message();
        message.setId(messageId);
        message.setTime(LocalDateTime.now());
        message.setAuthor(author);
        message.setRecipient(recipient);
        message.setMessageText(oldText);
        message.setStatus(status);
        message.setDialog(Mockito.mock(Dialog.class));
        message.setDeleted(false);

        Mockito.when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        Mockito.when(messageRepository.saveAndFlush(message)).thenReturn(message);

        MessageFullResponse messageFullResponse = messageService.editMessage(messageId, newText);

        Assert.assertNotNull(message);
        Assert.assertEquals(newText, message.getMessageText());
        Assert.assertNotNull(messageFullResponse);
        Assert.assertEquals(message.getId(), messageFullResponse.getId());
        Assert.assertEquals(message.getAuthor().getId(), messageFullResponse.getAuthorId());
        Assert.assertEquals((Long) message.getRecipient().getId(), messageFullResponse.getRecipientId());
        Assert.assertEquals(message.getMessageText(), messageFullResponse.getMessageText());
        Assert.assertEquals(status.name(), messageFullResponse.getReadStatus());
    }

    /**
     * Отправить сообщение
     */
    @Test
    public void sendMessage() {
        Person author = new Person();
        author.setId(4);

        long dialogId = 3;
        Dialog dialog = new Dialog();
        dialog.setId(dialogId);

        String messageText = "send new text";

        Message message = new Message();
        message.setId(1);
        message.setTime(LocalDateTime.now(TimeUtil.TIME_ZONE));
        message.setAuthor(author);
        message.setRecipient(null);
        message.setMessageText(messageText);
        message.setStatus(MessageStatus.SENT);
        message.setDialog(dialog);
        message.setDeleted(false);

        Mockito.when(dialogRepository.findById(dialogId)).thenReturn(Optional.of(dialog));
        Mockito.when(messageRepository.saveAndFlush(any())).thenReturn(message);

        MessageFullResponse messageFullResponse = messageService.sendMessage(author, dialogId, messageText);

        Assert.assertNotNull(messageFullResponse);
        Assert.assertEquals(message.getId(), messageFullResponse.getId());
        Assert.assertEquals(message.getAuthor().getId(), messageFullResponse.getAuthorId());
        Assert.assertNull(messageFullResponse.getRecipientId());
        Assert.assertEquals(message.getMessageText(), messageFullResponse.getMessageText());
        Assert.assertEquals(message.getStatus().name(), messageFullResponse.getReadStatus());

        Mockito.verify(dialogRepository, Mockito.times(1)).findById(dialogId);
        Mockito.verify(messageRepository, Mockito.times(1)).saveAndFlush(any());
    }
}