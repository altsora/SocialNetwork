package sn.model.dto.account.recoveryPassword;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Класс RecoveryFormData.
 * DTO для восстановления пароля.
 * @version 1.0
 */
@Data
@AllArgsConstructor
public class RecoveryFormData {

    private String token;
    private String password;

}
