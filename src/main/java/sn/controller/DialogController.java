package sn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.api.requests.MessageSendRequest;
import sn.api.response.*;
import sn.service.IMessageService;

@RestController
@RequestMapping("/dialogs")
public class DialogController {
    private final IMessageService messageService;

    @Autowired
    public DialogController(IMessageService messageService) {
        this.messageService = messageService;
    }

    @DeleteMapping("/{dialog_id}/messages/{message_id}")
    public ResponseEntity<ServiceResponse<AbstractResponse>> removeMessage(
            @PathVariable("dialog_id ") long dialogId,
            @PathVariable("message_id ") long messageId
    ) {
        if (!messageService.exists(messageId)) {
            ErrorResponse errorResponse = messageService.notFound(messageId);
            return ResponseEntity.badRequest().body(new ServiceResponse<>(errorResponse));
        }
        long id = messageService.removeMessage(messageId);
        MessageIdResponse messageIdResponse = MessageIdResponse.builder().messageId(id).build();
        return ResponseEntity.ok(new ServiceResponse<>(messageIdResponse));
    }

    @PutMapping("/{dialog_id}/messages/{message_id}")
    public ResponseEntity<ServiceResponse<AbstractResponse>> editMessage(
            @PathVariable("dialog_id ") long dialogId,
            @PathVariable("message_id ") long messageId,
            @RequestBody MessageSendRequest messageSendRequest
    ) {
        if (!messageService.exists(messageId)) {
            ErrorResponse errorResponse = messageService.notFound(messageId);
            return ResponseEntity.badRequest().body(new ServiceResponse<>(errorResponse));
        }
        MessageFullResponse messageFullResponse = messageService.editMessage(messageId, messageSendRequest.getMessageText());
        return ResponseEntity.ok(new ServiceResponse<>(messageFullResponse));
    }
}
