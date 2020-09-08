package sn.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sn.api.response.ResponseDataMessage;
import sn.api.response.ServiceResponse;

/**
 * Сервис для обработки ленты новостей
 */
@Service
public class FeedService {
    /**
     * Получение списка новостей
     * @param name Текст новости для поиска
     * @param offset Отступ от начала списка
     * @param itemPerPage Количество элементов на страницу
     * @return 200 - список новостей получен успешно, 400 - ошибка во время получения списка, 401 - ошибка авторизации.
     */
    public ResponseEntity<ServiceResponse> getFeeds(String name, int offset, int itemPerPage) {
        // TODO сменить, когда появится проверка авторизации
        boolean isNotAuthorized = false;
        // TODO сменить, успешное получение списка новостей
        boolean feedsIsOk = true;

        if (isNotAuthorized) {
            return new ResponseEntity<>(new ServiceResponse<>("invalid_request",
                    new ResponseDataMessage("User isn`t authorized")), HttpStatus.UNAUTHORIZED);
        }

        return feedsIsOk ? new ResponseEntity<>(new ServiceResponse(), HttpStatus.OK)
                : new ResponseEntity<>(new ServiceResponse<>("invalid_request",
                        new ResponseDataMessage("Unable to get feeds")), HttpStatus.BAD_REQUEST);
    }
}
