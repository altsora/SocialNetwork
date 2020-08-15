package sn.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.api.ResponseDataMessage;
import sn.api.requests.PersonEditRequest;
import sn.api.requests.WallPostRequest;
import sn.api.response.*;
import sn.model.Person;
import sn.model.Post;
import sn.service.ICommentService;
import sn.service.IPersonService;
import sn.service.IPostService;
import sn.utils.TimeUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class ProfileController {
    private final ICommentService commentService;
    private final IPersonService personService;
    private final IPostService postService;

    //==================================================================================================================

    /**
     * Получение текущего пользователя.
     * GET запрос /api/v1/users/me
     *
     * @return 200 - пользователь успешно получен; 401 - ошибка авторизации.
     */
    @GetMapping("/me")
    public ResponseEntity<ServiceResponse<AbstractResponse>> getCurrentUser() {
        if (!authService.isUserAuthorize()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ServiceResponse<>("Unauthorized", new ResponseDataMessage("User is not authorized")));
        }
        //TODO: получаем текущего авторизованного пользователя
        Person person = personService.findById(authService.getAuthorizedUserId());
        PersonResponse personResponse = personService.getPersonResponse(person);
        return ResponseEntity.ok(new ServiceResponse<>(personResponse));
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
        if (!authService.isUserAuthorize()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ServiceResponse<>("Unauthorized", new ResponseDataMessage("User is not authorized")));
        }
        Person person = personService.updatePerson(authService.getAuthorizedUserId(), personEditRequest);
        PersonResponse personResponse = personService.getPersonResponse(person);
        return ResponseEntity.ok(new ServiceResponse<>(personResponse));
    }

    /**
     * Удаление текущего пользователя.
     * DELETE запрос /api/v1/users/{id}
     *
     * @return 200 - пользователь удалён; 401 - ошибка авторизации.
     */
    @DeleteMapping("/me")
    public ResponseEntity<ServiceResponse<AbstractResponse>> deleteCurrentUser() {
        if (!authService.isUserAuthorize()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ServiceResponse<>("Unauthorized", new ResponseDataMessage("User is not authorized")));
        }
        personService.deleteById(authService.getAuthorizedUserId());
        return ResponseEntity.ok(new ServiceResponse<>(new ResponseDataMessage("ok")));
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
        Person person = personService.findById(personId);
        if (person == null) {
            return ResponseEntity.badRequest().body(new ServiceResponse<>("Bad request", new ResponseDataMessage("Service unavailable")));
        }
        PersonResponse personResponse = personService.getPersonResponse(person);
        return ResponseEntity.ok(new ServiceResponse<>(personResponse));
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
    public ResponseEntity<ServiceResponse<AbstractResponse>> getWallPosts(
            @PathVariable(value = "id") long personId,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20") int itemPerPage
    ) {
        if (!authService.isUserAuthorize()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ServiceResponse<>("Unauthorized", new ResponseDataMessage("User is not authorized")));
        }
        List<WallPostResponse> wallPosts = new ArrayList<>();
        Person person = personService.findById(personId);
        if (person == null) {
            return ResponseEntity.badRequest().body(new ServiceResponse<>("Bad request", new ResponseDataMessage("Service unavailable")));
        }
        List<Post> posts = postService.findAllByPersonId(personId, offset, itemPerPage);
        PersonResponse author = personService.getPersonResponse(person);
        for (Post post : posts) {
            List<CommentResponse> comments = commentService.getCommentsByPostId(post.getId());
            WallPostResponse wallPostResponse = postService.getExistsWallPost(post, author, comments);
            wallPosts.add(wallPostResponse);
        }
        int total = postService.getTotalCountPostsByPersonId(personId);
        //TODO: Список с постами не наследуется от AbstractResponse
        return ResponseEntity.ok(new ServiceResponse<>(total, offset, itemPerPage, wallPosts));
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
        if (!authService.isUserAuthorize()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ServiceResponse<>("Unauthorized", new ResponseDataMessage("User is not authorized")));
        }

        Person person = personService.findById(personId);
        if (person == null) {
            return ResponseEntity.badRequest()
                    .body(new ServiceResponse<>("Bad request", new ResponseDataMessage("Service unavailable")));
        }
        LocalDateTime postTime = publishDate != null ?
                TimeUtil.getLocalDateTimeFromTimestamp(publishDate) :
                LocalDateTime.now(TimeUtil.TIME_ZONE);
        String title = wallPostRequest.getTitle();
        String text = wallPostRequest.getPostText();
        Post post = postService.addPost(person, title, text, postTime);
        PersonResponse author = personService.getPersonResponse(person);
        WallPostResponse newPost = postService.createNewWallPost(post, author);
        return ResponseEntity.ok(new ServiceResponse<>(newPost));
    }

    /**
     * Поиск пользователей по указанным параметрам.
     * GET запрос /api/v1/users/search
     *
     * @param firstName   - Имя пользователей.
     * @param lastName    - Фамилия пользователей.
     * @param ageFrom     - Минимальный возраст пользователей.
     * @param ageTo       - Максимальный возраст пользователей.
     * @param countryId   - Идентификатор страны пользователей.
     * @param cityId      - Идентификатор города пользователей.
     * @param offset      - Отступ от начала результирующего списка пользователей.
     * @param itemPerPage - Количество пользователей из результирующего списка, которые представлены для отображения.
     * @return 200 - Возврат списка пользователей, подходящих по указанным параметрам;
     * 400 - произошла ошибка; 401 - ошибка авторизации.
     */
    @GetMapping("/search")
    public ResponseEntity<Object> findUser(
            @RequestParam(value = "first_name", required = false) String firstName,
            @RequestParam(value = "last_name", required = false) String lastName,
            @RequestParam(value = "age_from", required = false) Integer ageFrom,
            @RequestParam(value = "age_to", required = false) Integer ageTo,
            @RequestParam(value = "country_id", required = false) Integer countryId,
            @RequestParam(value = "city_id", required = false) Integer cityId,
            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20") Integer itemPerPage
    ) {
        //TODO: без учёта города и страны
        List<Person> personList = personService.searchPersons(firstName, lastName, ageFrom, ageTo, offset, itemPerPage);
        List<PersonResponse> searchResult = new ArrayList<>();
        for (Person person : personList) {
            searchResult.add(personService.getPersonResponse(person));
        }

        int total = personService.getTotalCountUsers();
        //TODO: По заданию фронт ожидает список в поле data
        return ResponseEntity.ok(new ServiceResponse<>(total, offset, itemPerPage, searchResult));
    }

    /**
     * Блокировка пользователя.
     * PUT запрос /api/v1/users/block/{id}
     *
     * @param personId - ID пользователя, которого надо заблокировать.
     * @return 200 - пользователь заблокирован; 400 - произошла ошибка; 401 - ошибка авторизации.
     */
    @PutMapping("/block/{id}")
    public ResponseEntity<ServiceResponse<AbstractResponse>> blockUserById(@PathVariable(value = "id") long personId) {
        if (!authService.isUserAuthorize()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ServiceResponse<>("Unauthorized", new ResponseDataMessage("User is not authorized")));
        }
        return personService.changeUserLockStatus(personId) ?
                ResponseEntity.ok(new ServiceResponse<>(new ResponseDataMessage("ok"))) :
                ResponseEntity.badRequest().body(new ServiceResponse<>("Bad request", new ResponseDataMessage("Service unavailable")));
    }

    /**
     * Разблокировка пользователя.
     * DELETE запрос /api/v1/users/block/{id}
     *
     * @param personId - ID пользователя, которого надо разблокировать.
     * @return 200 - пользователь разблокирован; 400 - произошла ошибка; 401 - ошибка авторизации.
     */
    @DeleteMapping("/block/{id}")
    public ResponseEntity<ServiceResponse<AbstractResponse>> unblockUserById(@PathVariable(value = "id") long personId) {
        if (!authService.isUserAuthorize()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ServiceResponse<>("Unauthorized", new ResponseDataMessage("User is not authorized")));
        }
        return personService.changeUserLockStatus(personId) ?
                ResponseEntity.ok(new ServiceResponse<>(new ResponseDataMessage("ok"))) :
                ResponseEntity.badRequest().body(new ServiceResponse<>("Bad request", new ResponseDataMessage("Service unavailable")));
    }
}
