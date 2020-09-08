package sn.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sn.api.response.ErrorResponse;
import sn.api.response.MessageFullResponse;
import sn.model.Message;
import sn.model.Person;
import sn.model.enums.MessageStatus;
import sn.repositories.MessageRepository;
import sn.service.IDialogService;
import sn.service.IMessageService;
import sn.utils.TimeUtil;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageService implements IMessageService {
    private final IDialogService dialogService;
    private final MessageRepository messageRepository;

    //==================================================================================================================

    @Override
    public Message findById(long messageId) {
        return messageRepository.findById(messageId).orElse(null);
    }

    @Override
    public boolean exists(long messageId) {
        return messageRepository.existsById(messageId);
    }

    @Override
    public long removeMessage(long messageId) {
        Message message = findById(messageId);
        message.setDeleted(true);
        return messageRepository.saveAndFlush(message).getId();
    }

    @Override
    public MessageFullResponse recoverMessage(long messageId) {
        Message message = findById(messageId);
        message.setDeleted(false);
        return getMessageFullResponse(messageRepository.saveAndFlush(message));
    }

    @Override
    public void readMessage(long messageId) {
        Message message = findById(messageId);
        message.setStatus(MessageStatus.READ);
        messageRepository.saveAndFlush(message);
    }

    @Override
    public MessageFullResponse editMessage(long messageId, String messageText) {
        Message message = findById(messageId);
        message.setMessageText(messageText);
        message = messageRepository.saveAndFlush(message);
        return getMessageFullResponse(message);
    }

    @Override
    public MessageFullResponse sendMessage(Person author, long dialogId, String messageText) {
        Message message = new Message();
        message.setTime(LocalDateTime.now(TimeUtil.TIME_ZONE));
        message.setAuthor(author);
        //TODO: пока не ясно, кто получатель
        message.setRecipient(null);
        message.setMessageText(messageText);
        message.setStatus(MessageStatus.SENT);
        message.setDialog(dialogService.findById(dialogId));
        message.setDeleted(false);

        message = messageRepository.saveAndFlush(message);
        return getMessageFullResponse(message);
    }

    private MessageFullResponse getMessageFullResponse(Message message) {
        return MessageFullResponse.builder()
                .id(message.getId())
                .time(TimeUtil.getTimestampFromLocalDateTime(message.getTime()))
                .authorId(message.getAuthor().getId())
                .recipientId(message.getRecipient() != null ? message.getRecipient().getId() : null)
                .messageText(message.getMessageText())
                .readStatus(message.getStatus().name())
                .build();
    }
}
