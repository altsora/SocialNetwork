package sn.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sn.api.response.ErrorResponse;
import sn.api.response.UserActivityResponse;
import sn.model.Dialog;
import sn.model.Person;
import sn.model.Person2Dialog;
import sn.repositories.DialogRepository;
import sn.repositories.Person2DialogRepository;
import sn.service.IDialogService;
import sn.utils.TimeUtil;

@Service
@RequiredArgsConstructor
public class DialogService implements IDialogService {
    private final DialogRepository dialogRepository;
    private final Person2DialogRepository person2DialogRepository;

    //==================================================================================================================

    @Override
    public Dialog findById(long dialogId) {
        return dialogRepository.findById(dialogId).orElse(null);
    }

    @Override
    public boolean exists(long dialogId) {
        return dialogRepository.existsById(dialogId);
    }

    @Override
    public boolean userExistsInDialog(long personId, long dialogId) {
        return person2DialogRepository.find(personId, dialogId) != null;
    }

    @Override
    public void decreaseUnreadCount(long dialogId) {
        Dialog dialog = findById(dialogId);
        dialog.setUnreadCount(dialog.getUnreadCount() - 1);
        dialogRepository.saveAndFlush(dialog);
    }

    @Override
    public UserActivityResponse getActivity(long personId, long dialogId) {
        Person2Dialog person2Dialog = person2DialogRepository.find(personId, dialogId);
        Person person = person2Dialog.getPerson();
        return UserActivityResponse.builder()
                .online(person.isOnline())
                .lastActivity(TimeUtil.getTimestampFromLocalDateTime(person.getLastOnlineTime()))
                .build();
    }


}
