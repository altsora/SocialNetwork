package sn.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.api.requests.PersonEditRequest;
import sn.api.requests.WallPostRequest;
import sn.api.response.*;
import sn.service.AccountService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class ProfileController {
    private final AccountService accountService;

    //==================================================================================================================

    /**
     * Получение текущего пользователя.
     * GET запрос /api/v1/users/me
     *
     * @return 200 - пользователь успешно получен; 401 - ошибка авторизации.
     */
    @GetMapping("/me")
    public ResponseEntity<ServiceResponse<AbstractResponse>> getCurrentUser() {
        return accountService.getCurrentUser();
    }

    /**
     * Редактирование текущего пользователя.
     * PUT запрос /api/v1/users/me
     *
     * @param personEditRequest - тело запроса в формате JSON. Содержит данные новые данные пользователя.
     * @return 200 - пользователь успешно отредактирован; 401 - ошибка авторизации.
     */
    @PutMapping("/me")
    public ResponseEntity<ServiceResponse<AbstractResponse>> editCurrentUser(@RequestBody PersonEditRequest personEditRequest) {
        return accountService.editUser(personEditRequest);
    }

    /**
     * Удаление текущего пользователя.
     * DELETE запрос /api/v1/users/{id}
     *
     * @return 200 - пользователь удалён; 401 - ошибка авторизации.
     */
    @DeleteMapping("/me")
    public ResponseEntity<ServiceResponse<AbstractResponse>> deleteCurrentUser() {
        return accountService.deleteUser();
    }

    /**
     * Получить пользователя по id.
     * GET запрос /api/v1/users/{id}
     *
     * @param personId - ID пользователя, которого надо получить.
     * @return 200 - получение пользователя по указанному идентификатору;
     * 400 - произошла ошибка; 401 - ошибка авторизации.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse<AbstractResponse>> getUserById(@PathVariable(value = "id") long personId) {
        return accountService.getUserById(personId);
    }

    /**
     * Получение записей на стене пользователя.
     * GET запрос /api/v1/users/{id}/wall
     *
     * @param personId    - ID пользователя, со стены которого требуется получить записи.
     * @param offset      - Отступ от начала результирующего списка публикаций.
     * @param itemPerPage - Количество публикаций из результирующего списка, которые представлены для отображения.
     * @return 200 - получение результирующего списка с публикациями на стене пользователя;
     * 400 - произошла ошибка; 401 - ошибка авторизации.
     */
    @GetMapping("/{id}/wall")
    public ResponseEntity<ServiceResponseDataList<WallPostResponse>> getWallPosts(
            @PathVariable(value = "id") long personId,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20") int itemPerPage
    ) {
        return accountService.getWallPosts(personId, offset, itemPerPage);
    }

    /**
     * Добавление публикации на стену пользователя.
     * POST запрос /api/v1/users/{id}/wall
     *
     * @param personId        - ID пользователя, который публикует записи.
     * @param publishDate     - Дата публикации, установленная пользователем.
     * @param wallPostRequest - тело запроса в формате JSON. Содержит данные о новой публикации.
     * @return 200 - запись готова к публикации к назначенному времени;
     * 400 - произошла ошибка; 401 - ошибка авторизации.
     */
    @PostMapping("/{id}/wall")
    public ResponseEntity<ServiceResponse<AbstractResponse>> addWallPost(
            @PathVariable(value = "id") long personId,
            @RequestParam(value = "publish_date", required = false) Long publishDate,
            @RequestBody WallPostRequest wallPostRequest
    ) {
        return accountService.addWallPost(personId, publishDate, wallPostRequest);
    }

    /**
     * Поиск пользователей по указанным параметрам.
     * GET запрос /api/v1/users/search
     *
     * @param firstName   - Имя пользователей.
     * @param lastName    - Фамилия пользователей.
     * @param city        - Город пользователя.
     * @param country     - Страна пользователя.
     * @param ageFrom     - Минимальный возраст пользователей.
     * @param ageTo       - Максимальный возраст пользователей.
     * @param offset      - Отступ от начала результирующего списка пользователей.
     * @param itemPerPage - Количество пользователей из результирующего списка, которые представлены для отображения.
     * @return 200 - Возврат списка пользователей, подходящих по указанным параметрам;
     * 400 - произошла ошибка; 401 - ошибка авторизации.
     */
    @GetMapping("/search")
    public ResponseEntity<ServiceResponseDataList<PersonResponse>> findUsers(
            @RequestParam(value = "first_name", required = false) String firstName,
            @RequestParam(value = "last_name", required = false) String lastName,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "age_from", required = false) Integer ageFrom,
            @RequestParam(value = "age_to", required = false) Integer ageTo,
            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20") Integer itemPerPage
    ) {
        return accountService.findUsers(firstName, lastName, city, country, ageFrom, ageTo, offset, itemPerPage);
    }

    /**
     * Блокировка пользователя.
     * PUT запрос /api/v1/users/block/{id}
     *
     * @param personId - ID пользователя, которого надо заблокировать.
     * @return 200 - пользователь заблокирован; 400 - произошла ошибка.
     */
    @PutMapping("/block/{id}")
    public ResponseEntity<ServiceResponse<AbstractResponse>> blockUserById(@PathVariable(value = "id") long personId) {
        return accountService.changeUserLockStatus(personId);
    }

    /**
     * Разблокировка пользователя.
     * DELETE запрос /api/v1/users/block/{id}
     *
     * @param personId - ID пользователя, которого надо разблокировать.
     * @return 200 - пользователь разблокирован; 400 - произошла ошибка.
     */
    @DeleteMapping("/block/{id}")
    public ResponseEntity<ServiceResponse<AbstractResponse>> unblockUserById(@PathVariable(value = "id") long personId) {
        return accountService.changeUserLockStatus(personId);
    }
}
