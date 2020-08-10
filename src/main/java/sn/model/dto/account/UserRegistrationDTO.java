package sn.model.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Field;

/**
 * Класс UserRegistrationDTO.
 * DTO for registration.
 * @version 1.0
 */

@Data
@AllArgsConstructor
public class UserRegistrationDTO {
    private String email;
    private String passwd1;
    private String passwd2;
    private String firstName;
    private String lastName;
    private String code;

    public boolean selfCheck() throws IllegalAccessException {
        Field[] fields = this.getClass().getFields();
        for (Field f : fields) {
            if (((String) f.get(this)).isEmpty() || f.get(this) == null) {
                return false;
            }
        }
        return true;
    }

}


