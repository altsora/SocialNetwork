package sn.model.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Класс EmailDTO.
 * Передача мыла пользвоателя с формы на фронте.
 *
 * @version 1.0
 */
@Data
@AllArgsConstructor
public class EmailDTO {
    private String email;
}
