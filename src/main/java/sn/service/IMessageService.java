package sn.service;

import sn.api.response.MessageFullResponse;
import sn.model.Message;
import sn.model.Person;

public interface IMessageService {
    Message findById(long messageId);

    boolean exists(long messageId);

    long removeMessage(long messageId);

    MessageFullResponse editMessage(long messageId, String messageText);

    MessageFullResponse sendMessage(Person author, long dialogId, String messageText);

    MessageFullResponse recoverMessage(long messageId);

    void readMessage(long messageId);
}
