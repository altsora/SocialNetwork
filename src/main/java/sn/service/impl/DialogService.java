package sn.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sn.model.Dialog;
import sn.repositories.DialogRepository;
import sn.service.IDialogService;

@Service
@RequiredArgsConstructor
public class DialogService implements IDialogService {
    private final DialogRepository dialogRepository;

    //==================================================================================================================

    @Override
    public Dialog findById(long dialogId) {
        return dialogRepository.findById(dialogId).orElse(null);
    }
}
