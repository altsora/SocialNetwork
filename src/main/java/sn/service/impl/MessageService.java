package sn.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sn.api.response.ErrorResponse;
import sn.api.response.MessageFullResponse;
import sn.model.Message;
import sn.repositories.MessageRepository;
import sn.service.IMessageService;
import sn.utils.TimeUtil;

@Service
@RequiredArgsConstructor
public class MessageService implements IMessageService {
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
        //TODO: Удалять ли сообщение из диалога?
        message.setDeleted(true);
        return messageRepository.saveAndFlush(message).getId();
    }

    @Override
    public MessageFullResponse editMessage(long messageId, String messageText) {
        Message message = findById(messageId);
        message.setMessageText(messageText);
        message = messageRepository.saveAndFlush(message);
        return MessageFullResponse.builder()
                .id(message.getId())
                .time(TimeUtil.getTimestampFromLocalDateTime(message.getTime()))
                .authorId(message.getAuthor().getId())
                .recipientId(message.getRecipient() != null ? message.getRecipient().getId() : null)
                .messageText(message.getMessageText())
                .readStatus(message.getStatus().name())
                .build();
    }


    @Override
    public ErrorResponse notFound(long messageId) {
        return ErrorResponse.builder()
                .error("Bad request")
                .errorDescription("Message with ID = " + messageId + " not found")
                .build();
    }
}
