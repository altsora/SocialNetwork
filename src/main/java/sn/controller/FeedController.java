package sn.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.api.response.ServiceResponse;

/**
 * REST-контроллер для работы с лентой новостей.
 */
@RestController
@RequestMapping("/feeds")
public class FeedController {
    private final FeedController feedController;

    public FeedController(FeedController feedController) {
        this.feedController = feedController;
    }

    /**
     * Получение списка новостей
     * @param name Текст новости для поиска
     * @param offset Отступ от начала списка
     * @param itemPerPage Количество элементов на страницу
     * @return 200 - список новостей получен успешно, 400 - ошибка во время получения списка, 401 - ошибка авторизации.
     */
    @GetMapping
    public ResponseEntity<ServiceResponse> getFeeds(String name, int offset, int itemPerPage) {
        return feedController.getFeeds(name, offset, itemPerPage);
    }
}
