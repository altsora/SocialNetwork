package sn.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sn.model.Message;
import sn.repositories.MessageRepository;
import sn.service.IMessageService;

@Service
@RequiredArgsConstructor
public class MessageService implements IMessageService {
    private final MessageRepository messageRepository;

    //==================================================================================================================

    @Override
    public Message findById(long messageId) {
        return messageRepository.findById(messageId).orElse(null);
    }
}
