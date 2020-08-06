package sn.service;

import sn.model.dto.account.UserRegistrationDTO;

/**
 * Интерфейс IAccountService.
 * Методы для работы с Account.
 */
public interface IAccountService {
    boolean setNewPassword(String password);
    boolean changeEmail(String newEmail);
    boolean register(UserRegistrationDTO userRegistrationDTO);
    boolean recoveryPassword(String email);

}
