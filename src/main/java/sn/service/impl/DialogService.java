package sn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sn.api.response.DialogResponse;
import sn.api.response.ServiceResponse;
import sn.controller.DialogController;
import sn.model.Dialog;
import sn.model.Message;
import sn.model.Person;
import sn.model.Person2Dialog;
import sn.model.enums.MessageStatus;
import sn.repositories.DialogRepository;
import sn.repositories.Person2DialogRepository;
import sn.repositories.PersonRepository;
import sn.service.IDialogService;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DialogService implements IDialogService {

    @Autowired
    private DialogRepository dialogRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private Person2DialogRepository person2DialogRepository;


    @Override
    public Dialog findById(long dialogId) {
        return dialogRepository.findById(dialogId).orElse(null);
    }

    @Override
    public ResponseEntity<ServiceResponse<DialogResponse>> findPersonDialogsWithQuery(
            String query, int offSet, int itemPerPage) {
        Person person = accountService.findCurrentUser();
        if (person == null) {
            log.error("person is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("user is not authorized", null));
        }
        if (person.getDialogs().isEmpty()) {
            log.warn("person has no dialogs");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("person has no dialogs", null));
        }
        List<DialogResponse.DialogData> dialogDataList = getDialogDataList(query, person);
        ServiceResponse<DialogResponse> serviceResponse =
                new ServiceResponse<DialogResponse>(DialogResponse.builder().dialogDataSet(dialogDataList).build());
        serviceResponse.setTotal(dialogDataList.size());
        serviceResponse.setOffset(offSet);
        serviceResponse.setPerPage(itemPerPage);
        return ResponseEntity.status(HttpStatus.OK).body(serviceResponse);
    }

    @Override
    public ResponseEntity<ServiceResponse<DialogResponse>> createDialog(DialogController.UserIdsRequest request) {
        Person person = accountService.findCurrentUser();
        List<Long> userIds = request.getUserIds();
        if (person == null) {
            log.error("user is not authorized");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("user is not authorized", null));
        }
        if (userIds.isEmpty()) {
            log.warn("recipients not specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("recipients not specified", null));
        }
        List<Person> recipients = personRepository.findAllById(userIds).stream()
                .filter(recipient -> recipient.getId() != person.getId()).collect(Collectors.toList());
        if (recipients.isEmpty()) {
            log.warn("recipients not specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("recipients not specified", null));
        }

        Dialog dialog = this.createEmptyDialog(person);
        Set<Person2Dialog> person2DialogSet = new HashSet<>();
        recipients.forEach(recipient -> person2DialogSet.add(new Person2Dialog(recipient, dialog)));
        person2DialogSet.add(new Person2Dialog(person, dialog));
        person2DialogRepository.saveAll(person2DialogSet);
        ServiceResponse<DialogResponse> serviceResponse =
                new ServiceResponse<DialogResponse>(DialogResponse.builder().dialogId(dialog.getId()).build());
        return ResponseEntity.status(HttpStatus.OK).body(serviceResponse);
    }

    @Override
    public ResponseEntity<ServiceResponse<DialogResponse>> getUnreadMessagesCount() {
        Person person = accountService.findCurrentUser();
        if (person == null) {
            log.error("user is not authorized");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("user is not authorized", null));
        }
        long unreadedMessagesCount = person.getReceivedMessages().stream()
                .filter(message -> message.getStatus().equals(MessageStatus.SENT)).count();
        ServiceResponse<DialogResponse> serviceResponse =
                new ServiceResponse<DialogResponse>(DialogResponse.builder()
                        .unreadedMessagesCount(unreadedMessagesCount).build());
        return ResponseEntity.status(HttpStatus.OK).body(serviceResponse);
    }

    @Override
    public ResponseEntity<ServiceResponse<DialogResponse>> deleteDialog(long dialogId) {
        Person person = accountService.findCurrentUser();
        if (person == null) {
            log.error("user is not authorized");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("user is not authorized", null));
        }
        if (dialogRepository.findById(dialogId).isEmpty()) {
            log.warn("dialog id:{} not found", dialogId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("dialog id:" + dialogId + " not found", null));
        }
        person2DialogRepository.deleleByPersonIdAndDialogId(person.getId(), dialogId);

        ServiceResponse<DialogResponse> serviceResponse =
                new ServiceResponse<DialogResponse>(DialogResponse.builder().dialogId(dialogId).build());
        return ResponseEntity.status(HttpStatus.OK).body(serviceResponse);
    }

    @Override
    public ResponseEntity<ServiceResponse<DialogResponse>> addUsersToDialog(
            long dialogId, DialogController.UserIdsRequest request) {
        Person person = accountService.findCurrentUser();
        List<Long> userIds = request.getUserIds();
        if (person == null) {
            log.error("user is not authorized");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("user is not authorized", null));
        }
        if (userIds.isEmpty()) {
            log.warn("recipients not specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("recipients not specified", null));
        }
        Optional<Dialog> dialogOpt = dialogRepository.findById(dialogId);
        if (dialogRepository.findById(dialogId).isEmpty()) {
            log.warn("dialog id:{} not found", dialogId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("dialog id:" + dialogId + " not found", null));
        }
        Dialog dialog = dialogOpt.get();
        List<Person> recipients = personRepository.findAllById(userIds).stream()
                .filter(recipient -> recipient.getId() != person.getId()).collect(Collectors.toList());
        if (recipients.isEmpty()) {
            log.warn("recipients not specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("recipients not specified", null));
        }

        Set<Person2Dialog> person2DialogSet = dialog.getPersons();
        recipients.forEach(recipient -> person2DialogSet.add(new Person2Dialog(recipient, dialog)));
        person2DialogSet.add(new Person2Dialog(person, dialog));
        person2DialogRepository.saveAll(person2DialogSet);
        dialog.setPersons(person2DialogSet);
        dialogRepository.save(dialog);
        ServiceResponse<DialogResponse> serviceResponse =
                new ServiceResponse<DialogResponse>(DialogResponse.builder().userIds(recipients.stream()
                        .map(Person::getId).collect(Collectors.toList())).build());
        return ResponseEntity.status(HttpStatus.OK).body(serviceResponse);
    }

    @Override
    public ResponseEntity<ServiceResponse<DialogResponse>> deleteUsersFromDialog(
            long dialogId, DialogController.UserIdsRequest request) {
        Person person = accountService.findCurrentUser();
        List<Long> userIds = request.getUserIds();
        if (person == null) {
            log.error("user is not authorized");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("user is not authorized", null));
        }
        if (userIds.isEmpty()) {
            log.warn("recipients not specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("recipients not specified", null));
        }
        Optional<Dialog> dialogOpt = dialogRepository.findById(dialogId);
        if (dialogRepository.findById(dialogId).isEmpty()) {
            log.warn("dialog id:{} not found", dialogId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("dialog id:" + dialogId + " not found", null));
        }
        Dialog dialog = dialogOpt.get();
        List<Person> recipients = personRepository.findAllById(userIds).stream()
                .filter(recipient -> recipient.getId() != person.getId()).collect(Collectors.toList());
        if (recipients.isEmpty()) {
            log.warn("recipients not specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("recipients not specified", null));
        }

        Set<Person2Dialog> person2DialogSet = dialog.getPersons();
        recipients.forEach(recipient -> person2DialogSet.remove(new Person2Dialog(recipient, dialog)));
        person2DialogRepository.deleteAll(person2DialogSet);
        dialogRepository.save(dialog);
        ServiceResponse<DialogResponse> serviceResponse =
                new ServiceResponse<DialogResponse>(DialogResponse.builder().userIds(recipients.stream()
                        .map(Person::getId).collect(Collectors.toList())).build());
        return ResponseEntity.status(HttpStatus.OK).body(serviceResponse);
    }

    @Override
    public ResponseEntity<ServiceResponse<DialogResponse>> createInviteLink(long dialogId) {
        Person person = accountService.findCurrentUser();
        if (person == null) {
            log.error("user is not authorized");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("user is not authorized", null));
        }
        if (person.getDialogs().stream().noneMatch(dialog -> dialog.getId() == dialogId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("dialog id:" + dialogId + "not found", null));
        }
        String inviteCode = UUID.randomUUID().toString();
        dialogRepository.findById(dialogId).ifPresent(dialog -> {
            dialog.setInviteCode(inviteCode);
            dialogRepository.save(dialog);
        });
        ServiceResponse<DialogResponse> serviceResponse =
                new ServiceResponse<DialogResponse>(DialogResponse.builder().inviteLink(inviteCode).build());
        return ResponseEntity.status(HttpStatus.OK).body(serviceResponse);
    }

    @Override
    public ResponseEntity<ServiceResponse<DialogResponse>> joinUserToDialog(long dialogId, String link) {
        Person person = accountService.findCurrentUser();
        if (person == null) {
            log.error("user is not authorized");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("user is not authorized", null));
        }
        if (!Strings.isNotEmpty(link)) {
            log.error("invite code is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("invite code is null or empty", null));
        }
        Dialog dialog = dialogRepository.findByInviteCode(link);
        if (dialog == null) {
            log.error("dialog not found by invite code [{}]", link);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("dialog not found", null));
        }
        Person2Dialog person2Dialog = person2DialogRepository.save(new Person2Dialog(person, dialog));
        ServiceResponse<DialogResponse> serviceResponse =
                new ServiceResponse<DialogResponse>(
                        DialogResponse.builder().userIds(Collections.singletonList(
                                person2Dialog.getPerson().getId())).build());
        return ResponseEntity.status(HttpStatus.OK).body(serviceResponse);
    }

    @Override
    public ResponseEntity<ServiceResponse<DialogResponse>> getDialogMessages(
            long dialogId, String query, int offset, int itemPerPage) {
        Person person = accountService.findCurrentUser();
        if (person == null) {
            log.error("user is not authorized");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("user is not authorized", null));
        }
        if (person.getDialogs().isEmpty()) {
            log.warn("person has no dialogs");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("person has no dialogs", null));
        }
        if (person.getDialogs().stream().noneMatch(dialog -> dialog.getId() == dialogId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("dialog id:" + dialogId + "not found", null));
        }
        Set<Message> dialogMessages = Strings.isNotEmpty(query) ?
                Objects.requireNonNull(dialogRepository.findById(dialogId).orElse(null)).getMessages().stream()
                .filter(message -> message.getMessageText().contains(query)).collect(Collectors.toSet()) :
                Objects.requireNonNull(dialogRepository.findById(dialogId).orElse(null)).getMessages();
        ServiceResponse<DialogResponse> serviceResponse =
                new ServiceResponse<DialogResponse>(
                        DialogResponse.builder().dialogMessages(dialogMessages).build());
        serviceResponse.setTotal(dialogMessages.size());
        serviceResponse.setOffset(offset);
        serviceResponse.setPerPage(itemPerPage);
        return ResponseEntity.status(HttpStatus.OK).body(serviceResponse);
    }

    /**
     * Метод getDialogDataList.
     * Фильтрация по запросу и формирование коллекции данных о диалогах пользователя.
     *
     * @param query  - строка, которую должны содержать сообщения пользователя.
     * @param person - пользователь.
     * @return коллекция данных о диалогах пользователя.
     */
    private List<DialogResponse.DialogData> getDialogDataList(String query, Person person) {
        return !Strings.isNotEmpty(query) ?
                person.getDialogs().stream()
                        .map(Person2Dialog::getDialog)
                        .map(this::createDialogData)
                        .collect(Collectors.toList()) :
                person.getDialogs().stream()
                        .filter(person2Dialog -> person2Dialog.getDialog().getMessages().stream()
                                .anyMatch(message -> message.getMessageText().contains(query))
                        ).map(Person2Dialog::getDialog)
                        .map(this::createDialogData)
                        .collect(Collectors.toList());
    }

    /**
     * Метод createDialogData.
     * Формирование тела ответа.
     *
     * @param dialog дилог.
     * @return тело ответа.
     */
    private DialogResponse.DialogData createDialogData(Dialog dialog) {
        if (dialog.getMessages().isEmpty()) {
            log.info("dialog id:{} has no messages", dialog.getId());
            return DialogResponse.DialogData.builder()
                    .dialogId(dialog.getId())
                    .unreadCount(dialog.getUnreadCount())
                    .message(null).build();
        }
        Message lastMessage = dialog.getMessages().stream()
                .max(Comparator.comparing(Message::getTime)).orElse(new Message());
        return DialogResponse.DialogData.builder()
                .dialogId(dialog.getId())
                .unreadCount(dialog.getUnreadCount())
                .message(DialogResponse.DialogData.LastMessage.builder()
                        .id(lastMessage.getId())
                        .time(Timestamp.valueOf(lastMessage.getTime()).getTime())
                        .authorId(lastMessage.getAuthor().getId())
                        .recipientId(lastMessage.getRecipient().getId())
                        .messageText(lastMessage.getMessageText())
                        .messageStatus(lastMessage.getStatus()).build()
                ).build();
    }


    private Dialog createEmptyDialog(Person owner) {
        Dialog newDialog = new Dialog();
        newDialog.setOwner(owner);
        newDialog.setDeleted(false);
        newDialog.setUnreadCount(0);
        return dialogRepository.save(newDialog);
    }

}
