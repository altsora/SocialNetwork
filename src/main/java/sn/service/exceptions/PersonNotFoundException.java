package sn.service.exceptions;

/**
 * Класс PersonNotFoundException.
 * Исключение когда пользователь не найден.
 */
public class PersonNotFoundException extends Exception{

    private String message;

    public PersonNotFoundException(String message) {
        super(message);
    }
}
