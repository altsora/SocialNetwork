package sn.service;

public interface IAccountService {

    boolean setNewPassword(String recoveryCode, String password);
    boolean changeEmail(String newEmail);
}
