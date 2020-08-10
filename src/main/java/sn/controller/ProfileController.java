package sn.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.api.requests.PersonEditBody;
import sn.api.requests.PostRequestBody;
import sn.api.response.*;
import sn.model.Person;
import sn.model.Post;
import sn.service.CommentService;
import sn.service.PostService;
import sn.utils.TimeUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class ProfileController {
    private final CommentService commentService;
    private final IPersonService personService;
    private final PostService postService;

    //==================================================================================================================

    /**
     * Метод getCurrentUser.
     * Получение текущего пользователя.
     * GET запрос /api/v1/users/me
     *
     * @return 200 - пользователь успешно получен;
     * 400 - произошла ошибка; 401 - ошибка авторизации.
     */
    @GetMapping("/me")
    public ResponseEntity<ServiceResponse<AbstractResponse>> getCurrentUser() {
        //TODO: проверка (в каком случае может быть отрицательный ответ?)
        if (false) {
            //TODO: нужно слияние
            return ResponseEntity.badRequest().
                    body(new ServiceResponse<>("Invalid request", new ResponseDataMessage("Service unavailable")));
        }

        //TODO: Здесь будет персон из сервиса, которого мы найдём
        // по ID текущего авторизованного пользователя
        Person person = new Person();

        //TODO: нет данных, откуда эти объекты берутся
        CityResponse city = new CityResponse("Москва");
        CountryResponse country = new CountryResponse("Россия");

        PersonResponse personResponse = PersonResponse.builder()
                .id(person.getId())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                //TODO: конвертировать время в long
                .regDate(TimeUtil.getTimestampFromLocalDateTime(person.getRegDate()))
                .birthDate(TimeUtil.getTimestampFromLocalDate(person.getBirthDate()))
                .email(person.getEmail())
                .phone(person.getPhone())
                .photo(person.getPhoto())
                .about(person.getAbout())
                .city(city)
                .country(country)
                .messagesPermission(person.getMessagesPermission())
                .lastOnlineTime(TimeUtil.getTimestampFromLocalDateTime(person.getLastOnlineTime()))
                .isBlocked(person.isBlocked())
                .build();

        //TODO: нужно слияние с веткой SN-8
        return ResponseEntity.ok(new ServiceResponse<>(personResponse));
    }

    /**
     * Метод editCurrentUser.
     * Редактирование текущего пользователя.
     * PUT запрос /api/v1/users/me
     *
     * @param personEditBody - тело запроса в формате JSON. Содержит данные новые данные пользователя.
     * @return 200 - пользователь успешно отредактирован;
     * 400 - произошла ошибка; 401 - ошибка авторизации.
     */
    @PutMapping("/me")
    public ResponseEntity<ServiceResponse<AbstractResponse>> editCurrentUser(@RequestBody PersonEditBody personEditBody) {
        //TODO: проверка (в каком случае может быть отрицательный ответ?)
        if (false) {
            //TODO: нужно слияние
            return ResponseEntity.badRequest().
                    body(new ServiceResponse<>("Invalid request", new ResponseDataMessage("Service unavailable")));
        }

        //TODO: найти человека в базе (узнать, по какому признаку искать);
        // обновляем пользователя в сервисе и заносим изменённого пользователя
        // в базу
        Person person = personService.updatePerson(personEditBody);

        person.setFirstName(personEditBody.getFirstName());
        person.setLastName(personEditBody.getLastName());
        //TODO: конвертировать время в long
//        person.setBirthDate(personForm.getBirthDate());
        person.setPhone(personEditBody.getPhone());
        //TODO: поиск и/или загрузка фото по его ID
        String photo = null;
        person.setPhoto(photo);
        person.setAbout(personEditBody.getAbout());
        //TODO: город и страна пока что без изменений, т.к. нет их хранилища
//        person.setCity();
//        person.setCountry();
        person.setMessagesPermission(personEditBody.getMessagesPermission());

        //TODO: нет данных, откуда эти объекты берутся.
        // По приходящим ID должны устанавливаться новые значения
        CityResponse city = new CityResponse("Москва");
        CountryResponse country = new CountryResponse("Россия");

        PersonResponse personResponse = PersonResponse.builder()
                .id(person.getId())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .regDate(TimeUtil.getTimestampFromLocalDateTime(person.getRegDate()))
                .birthDate(TimeUtil.getTimestampFromLocalDate(person.getBirthDate()))
                .email(person.getEmail())
                .phone(person.getPhone())
                .photo(person.getPhoto())
                .about(person.getAbout())
                .city(city)
                .country(country)
                .messagesPermission(person.getMessagesPermission())
                .lastOnlineTime(TimeUtil.getTimestampFromLocalDateTime(person.getLastOnlineTime()))
                .isBlocked(person.isBlocked())
                .build();

        //TODO: нужно слияние с веткой SN-8
        return ResponseEntity.ok(new ServiceResponse<>(personResponse));
    }

    /**
     * Метод getUserById.
     * Удаление текущего пользователя.
     * DELETE запрос /api/v1/users/{id}
     *
     * @return 200 - пользователь удалён;
     * 400 - произошла ошибка; 401 - ошибка авторизации.
     */
    @DeleteMapping("/me")
    public ResponseEntity<ServiceResponse<AbstractResponse>> deleteCurrentUser() {
        //TODO: Получить ID текущего пользователя и по нему удалять в базе
        boolean personDeleted = personService.deleteById(getCurrentUserId());
        return personDeleted ?
                ResponseEntity
                        .ok(new ServiceResponse<>(new ResponseDataMessage("ok"))) :
                ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ServiceResponse<>("Unauthorized", new ResponseDataMessage("User is not authorized")));
    }

    /**
     * Метод getUserById.
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

        //TODO: нет данных, откуда эти объекты берутся.
        // По названию нужно найти эти объекты (где-то)
        CityResponse city = unknowService.findByCityName(person.getCity());
        CountryResponse country = unknowService.findByCountryName(person.getCountry());

        PersonResponse personResponse = PersonResponse.builder()
                .id(person.getId())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .regDate(TimeUtil.getTimestampFromLocalDateTime(person.getRegDate()))
                .birthDate(TimeUtil.getTimestampFromLocalDate(person.getBirthDate()))
                .email(person.getEmail())
                .phone(person.getPhone())
                .photo(person.getPhoto())
                .about(person.getAbout())
                .city(city)
                .country(country)
                .messagesPermission(person.getMessagesPermission())
                .lastOnlineTime(TimeUtil.getTimestampFromLocalDateTime(person.getLastOnlineTime()))
                .isBlocked(person.isBlocked())
                .build();

        //TODO: нужно слияние с веткой SN-8
        return ResponseEntity.ok(new ServiceResponse<>(personResponse));
    }

    /**
     * Метод getWallEntriesByUserId.
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
    public ResponseEntity<ServiceResponse<AbstractResponse>> getWallEntriesByUserId(
            @PathVariable(value = "id") long personId,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20") int itemPerPage
    ) {
        //TODO: если пользователь не авторизован?
        if (false) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ServiceResponse<>("Unauthorized", new ResponseDataMessage("User is not authorized")));
        }
        List<WallPostResponse> wallPosts = new ArrayList<>();
        Person person = personService.findById(personId);
        if (person == null) {
            return ResponseEntity.badRequest().body(new ServiceResponse<>("Bad request", new ResponseDataMessage("Service unavailable")));
        }
        //TODO: нет данных, откуда эти объекты берутся.
        // По названию нужно найти эти объекты (где-то)
        CityResponse city = unknowService.findByCityName(person.getCity());
        CountryResponse country = unknowService.findByCountryName(person.getCountry());
        List<Post> posts = postService.findAllByPersonId(personId, offset, itemPerPage);
        PersonResponse author = PersonResponse.builder()
                .id(person.getId())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .regDate(TimeUtil.getTimestampFromLocalDateTime(person.getRegDate()))
                .birthDate(TimeUtil.getTimestampFromLocalDate(person.getBirthDate()))
                .email(person.getEmail())
                .phone(person.getPhone())
                .photo(person.getPhoto())
                .about(person.getAbout())
                .city(city)
                .country(country)
                .messagesPermission(person.getMessagesPermission())
                .lastOnlineTime(TimeUtil.getTimestampFromLocalDateTime(person.getLastOnlineTime()))
                .isBlocked(person.isBlocked())
                .build();
        for (Post post : posts) {
            List<CommentResponse> comments = commentService.getCommentsByPostId(post.getId());
            WallPostResponse wallPostResponse = WallPostResponse.builder()
                    .id(post.getId())
                    .time(TimeUtil.getTimestampFromLocalDateTime(post.getTime()))
                    .author(author)
                    .title(post.getTitle())
                    .postText(post.getText())
                    .isBlocked(post.isBlocked())
                    .likesCount(post.getLikesCount())
                    .comments(comments)
                    //TODO: как определять статус записи на стене?
//                    .statusWallPost()
                    .build();
            wallPosts.add(wallPostResponse);
        }
        int total = postService.getTotalCountPostsByPersonId(personId);
        //TODO: Список с постами не наследуется от AbstractResponse
        return ResponseEntity.ok(new ServiceResponse<>(total, offset, itemPerPage, wallPosts));
    }

    /**
     * Метод setWallEntriesByUserId.
     * Добавление публикации на стену пользователя.
     * POST запрос /api/v1/users/{id}/wall
     *
     * @param personId        - ID пользователя, который публикует записи.
     * @param publishDate     - Дата публикации, установленная пользователем.
     * @param postRequestBody - тело запроса в формате JSON. Содержит данные о новой публикации.
     * @return 200 - запись готова к публикации к назначенному времени;
     * 400 - произошла ошибка; 401 - ошибка авторизации.
     */
    @PostMapping("/{id}/wall")
    public ResponseEntity<ServiceResponse<AbstractResponse>> setWallEntriesByUserId(
            @PathVariable(value = "id") long personId,
            @RequestParam(value = "publish_date", required = false) Long publishDate,
            @RequestBody PostRequestBody postRequestBody
    ) {
        //TODO: если пользователь не авторизован?
        if (false) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ServiceResponse<>("Unauthorized", new ResponseDataMessage("User is not authorized")));
        }
        Person person = personService.findById(personId);
        if (person == null) {
            return ResponseEntity.badRequest().body(new ServiceResponse<>("Bad request", new ResponseDataMessage("Service unavailable")));
        }
        //TODO: нет данных, откуда эти объекты берутся.
        // По названию нужно найти эти объекты (где-то)
        LocalDateTime postTime = publishDate != null ?
                TimeUtil.getLocalDateTimeFromTimestamp(publishDate) :
                LocalDateTime.now(TimeUtil.TIME_ZONE);
        String title = postRequestBody.getTitle();
        String text = postRequestBody.getPostText();
        Post post = postService.addPost(person, title, text, postTime);
        CityResponse city = unknowService.findByCityName(person.getCity());
        CountryResponse country = unknowService.findByCountryName(person.getCountry());
        PersonResponse author = PersonResponse.builder()
                .id(person.getId())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .regDate(TimeUtil.getTimestampFromLocalDateTime(person.getRegDate()))
                .birthDate(TimeUtil.getTimestampFromLocalDate(person.getBirthDate()))
                .email(person.getEmail())
                .phone(person.getPhone())
                .photo(person.getPhoto())
                .about(person.getAbout())
                .city(city)
                .country(country)
                .messagesPermission(person.getMessagesPermission())
                .lastOnlineTime(TimeUtil.getTimestampFromLocalDateTime(person.getLastOnlineTime()))
                .isBlocked(person.isBlocked())
                .build();
        WallPostResponse newPost = WallPostResponse.builder()
                .id(post.getId())
                .time(TimeUtil.getTimestampFromLocalDateTime(post.getTime()))
                .author(author)
                .title(title)
                .postText(text)
                .isBlocked(false)
                .likesCount(0)
                .comments(new ArrayList<>())
                .build();

        return ResponseEntity.ok(new ServiceResponse<>(newPost));
    }

    /**
     * Метод findUser.
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

        //todo
        return null;
    }

    /**
     * Метод blockUserById.
     * Блокировка пользователя.
     * PUT запрос /api/v1/users/block/{id}
     *
     * @param personId - ID пользователя, которого надо заблокировать.
     * @return 200 - пользователь заблокирован; 400 - произошла ошибка; 401 - ошибка авторизации.
     */
    @PutMapping("/block/{id}")
    public ResponseEntity<ServiceResponse<AbstractResponse>> blockUserById(@PathVariable(value = "id") long personId) {
        Person person = personService.findById(personId);
        person.setBlocked(true);
        //TODO: ошибка авторизации
        if (false) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ServiceResponse<>("Unauthorized", new ResponseDataMessage("User is not authorized")));
        }
        //TODO: Если регистрация успешна - boolean method
        boolean block = personService.changeBlockStatus(personId);
        return block ?
                ResponseEntity.ok(new ServiceResponse<>(new ResponseDataMessage("ok"))) :
                ResponseEntity.badRequest().body(new ServiceResponse<>("Bad request", new ResponseDataMessage("Service unavailable")));
        ;
    }

    /**
     * Метод unblockUserById.
     * Разблокировка пользователя.
     * DELETE запрос /api/v1/users/block/{id}
     *
     * @param personId - ID пользователя, которого надо разблокировать.
     * @return 200 - пользователь разблокирован; 400 - произошла ошибка; 401 - ошибка авторизации.
     */
    @DeleteMapping("/block/{id}")
    public ResponseEntity<ServiceResponse<AbstractResponse>> unblockUserById(@PathVariable(value = "id") long personId) {
        Person person = personService.findById(personId);
        person.setBlocked(false);
        //TODO: ошибка авторизации
        if (false) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ServiceResponse<>("Unauthorized", new ResponseDataMessage("User is not authorized")));
        }
        //TODO: Если регистрация успешна - boolean method
        boolean unblock = personService.changeBlockStatus(personId);
        return unblock ?
                ResponseEntity.ok(new ServiceResponse<>(new ResponseDataMessage("ok"))) :
                ResponseEntity.badRequest().body(new ServiceResponse<>("Bad request", new ResponseDataMessage("Service unavailable")));
        ;
    }
}
