package sn.service;

import sn.model.Message;

public interface IMessageService {
    Message findById(long messageId);
}
