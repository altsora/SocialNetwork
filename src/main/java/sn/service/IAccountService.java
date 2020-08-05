package sn.service;

import sn.model.dto.account.UserRegistrationDTO;

public interface IAccountService {

    boolean setNewPassword(String password);
    boolean changeEmail(String newEmail);
    boolean register(UserRegistrationDTO userRegistrationDTO);
    boolean recoveryPassword(String email);

}
