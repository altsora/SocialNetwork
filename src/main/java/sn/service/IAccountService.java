package sn.service;

import sn.model.Person;
import sn.model.dto.account.UserRegistrationRequest;

/**
 * Интерфейс IAccountService.
 * Методы для работы с Account.
 */
public interface IAccountService {
    boolean setNewPassword(String password);
    boolean changeEmail(String newEmail);
    boolean register(UserRegistrationRequest userRegistrationRequest);
    boolean recoveryPassword(String email);

}
