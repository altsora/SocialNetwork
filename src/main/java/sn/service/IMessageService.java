package sn.service;

import sn.api.response.ErrorResponse;
import sn.api.response.MessageFullResponse;
import sn.model.Message;

public interface IMessageService {
    Message findById(long messageId);

    boolean exists(long messageId);

    long removeMessage(long messageId);

    MessageFullResponse editMessage(long messageId, String messageText);

    ErrorResponse notFound(long messageId);
}
