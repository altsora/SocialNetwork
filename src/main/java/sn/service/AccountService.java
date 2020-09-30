package sn.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sn.api.requests.PersonEditRequest;
import sn.api.requests.WallPostRequest;
import sn.api.response.*;
import sn.model.Person;
import sn.model.Post;
import sn.model.dto.account.UserRegistrationRequest;
import sn.repositories.PersonRepository;
import sn.utils.ErrorUtil;
import sn.utils.TimeUtil;

import javax.transaction.Transactional;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Класс AccountService.
 * Сервисный слой для аккаунта пользователя.
 *
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final Authentication authentication;
    private final MailSenderService mailSenderService;
    private final PostService postService;
    private final CommentService commentService;

    public boolean exists(long personId) {
        return personRepository.existsById(personId);
    }

    /**
     * Формирует PersonResponse на основе Person.
     *
     * @param person - объект класса Person.
     * @return - возврат true, если статус изменён, иначе false.
     */
    public PersonResponse getPersonResponse(Person person) {
        //TODO: Нет данных, откуда берутся город и страна. Если логика стран и городов заработает - изменить соответствующие поля
        return PersonResponse.builder()
                .id(person.getId())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .regDate(TimeUtil.getTimestampFromLocalDateTime(person.getRegDate()))
                .birthDate(TimeUtil.getTimestampFromLocalDate(person.getBirthDate()))
                .email(person.getEmail())
                .phone(person.getPhone())
                .photo(person.getPhoto())
                .about(person.getAbout())
                .city(new CityResponse("Москва"))
                .country(new CountryResponse("Россия"))
                .messagesPermission(person.getMessagesPermission())
                .lastOnlineTime(TimeUtil.getTimestampFromLocalDateTime(person.getLastOnlineTime()))
                .isBlocked(person.isBlocked())
                .build();
    }

    /**
     * Метод register.
     * Регистрация нового пользователя.
     *
     * @param userRegistrationRequest - данные с веб-формы.
     * @return true - если пользователь сохранен в базу, false - не совпали пароли, есть пользователь с такой же почтой,
     * база данных недоступна.
     * @see UserRegistrationRequest ;
     */
    public boolean register(UserRegistrationRequest userRegistrationRequest) {
        if (!userRegistrationRequest.getPasswd1().equals(userRegistrationRequest.getPasswd2())) {
            log.warn("Passwords mismatch. Registration cancelled.");
            return false;
        }
        if (personRepository.findByEmail(userRegistrationRequest.getEmail()).isPresent()) {
            log.error("User [{}] is exists.", userRegistrationRequest.getEmail());
            return false;
        }
        Person person = new Person();
        person.setFirstName(userRegistrationRequest.getFirstName());
        person.setLastName(userRegistrationRequest.getLastName());
        person.setPassword(passwordEncoder.encode(userRegistrationRequest.getPasswd1()));
        person.setEmail(userRegistrationRequest.getEmail());
        personRepository.save(person);
        log.info("Person successfully registered");
        return true;
    }

    /**
     * Метод recoveryPassword.
     * Метод для восстановления пароля.
     *
     * @param email почта пользователя.
     * @return true - новый пароль сохранен и отправлен пользователю, false - person не найден в базе, база недоступна.
     */
    public boolean recoveryPassword(String email) {
        Optional<Person> personOpt = personRepository.findByEmail(email);
        if (personOpt.isEmpty()) {
            log.warn("Person [{}] not found for password recovery.", email);
            return false;
        }
        Person person = personOpt.get();
        String newPassword = generateNewPassword(9);
        person.setPassword(passwordEncoder.encode(newPassword));
        personRepository.save(person);
        log.info("New password set to the person.");
        CompletableFuture.runAsync(() -> mailSenderService.send(person.getEmail(), "Password recovery",
                "Your new password: " + newPassword));
        return true;
    }

    /**
     * Метод setNewPassword.
     * Осознанная смена пароля.
     *
     * @param password новый пароль.
     * @return true - если пароль изменен, false - если пользователь не найден в базе, база недоступна.
     */
    @Transactional
    public boolean setNewPassword(String password) {
        Optional<Person> personOpt = personRepository.findByEmail(authentication.getName());
        if (personOpt.isEmpty()) {
            log.warn("Person not found by email [{}].", authentication.getName());
            return false;
        }
        Person person = personOpt.get();
        person.setPassword(passwordEncoder.encode(password));
        personRepository.save(person);
        log.info("Person password successfully recovered.");
        CompletableFuture.runAsync(() -> mailSenderService.send(person.getEmail(), "Password recovery",
                new StringBuffer("Your password has been changed successfully from ")
                        .append(getIpAddress()).append(" at ").append(LocalDateTime.now()).toString()));
        return true;
    }

    /**
     * Метод changeEmail.
     * Осознанная смена почтового адреса.
     *
     * @param newEmail новый адрес почты.
     * @return true - если адрес измене, false - если существует пользователь с таким же почтовым адресом,
     * пользователь не найден, база недоступна.
     */
    @Transactional
    public boolean changeEmail(String newEmail) {
        Optional<Person> personOpt = personRepository.findByEmail(authentication.getName());
        if (personOpt.isEmpty()) {
            log.warn("Person not found by email [{}].", authentication.getName());
            return false;
        }
        Person person = personOpt.get();
        person.setEmail(newEmail);
        personRepository.save(person);
        log.info("Person email successfully changed.");
        return true;
    }

    /**
     * Метод findCurrentUser.
     * Получение текущего пользователя.
     *
     * @return Person или null, если текущий пользователь не аутентифицирован.
     */
    public Person findCurrentUser() {
        if (authentication instanceof AnonymousAuthenticationToken) {
            log.warn("Anonymous user authenticated");
            return null;
        }
        Optional<Person> personOpt = personRepository.findByEmail(authentication.getName());
        if (personOpt.isEmpty()) {
            log.warn("Person not found by email [{}].", authentication.getName());
            return null;
        }
        return personOpt.get();
    }

    /**
     * Метод generateNewPassword.
     * Генератор пароля.
     *
     * @param length длина пароля.
     * @return пароль.
     */
    private String generateNewPassword(int length) {
        int leftLimit = 48;
        int rightLimit = 122;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    /**
     * Метод getIpAddress.
     * Получение ip-адреса пользователя.
     *
     * @return ip-алрес пользователя.
     */
    private InetAddress getIpAddress() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            log.error("Can't get ip address.");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Получение текущего пользователя.
     * Пользователь должен быть авторизован.
     *
     * @return 200 - пользователь успешно получен; 401 - ошибка авторизации.
     */
    public ResponseEntity<ServiceResponse<AbstractResponse>> getCurrentUser() {
        Person person = findCurrentUser();
        if (person == null) {
            log.error("Unauthorized access");
            return ErrorUtil.unauthorized();
        }
        return ResponseEntity.ok(new ServiceResponse<>(getPersonResponse(person)));
    }

    /**
     * Редактирование текущего пользователя - обновление данных.
     * Пользователь должен быть авторизован.
     *
     * @param personEditRequest - тело запроса в формате JSON. Содержит данные новые данные пользователя.
     * @return 200 - пользователь успешно отредактирован; 401 - ошибка авторизации.
     */
    public ResponseEntity<ServiceResponse<AbstractResponse>> editUser(PersonEditRequest personEditRequest) {
        Person person = findCurrentUser();
        if (person == null) {
            log.error("Unauthorized access");
            return ErrorUtil.unauthorized();
        }
        person.setFirstName(personEditRequest.getFirstName());
        person.setLastName(personEditRequest.getLastName());
        person.setBirthDate(TimeUtil.getLocalDateFromTimestamp(personEditRequest.getBirthDate()));
        person.setPhone(personEditRequest.getPhone());
        person.setPhoto(personEditRequest.getPhoto());
        person.setAbout(personEditRequest.getAbout());
        //TODO: город и страна без изменений. Если логика с городами и странами заработает - добавить соответствующие сеттеры
        person.setMessagesPermission(personEditRequest.getMessagesPermission());
        person = personRepository.saveAndFlush(person);
        log.info("Update data for user with id {}.", person.getId());
        return ResponseEntity.ok(new ServiceResponse<>(getPersonResponse(person)));
    }

    /**
     * Удаление текущего пользователя.
     * Пользователь должен быть авторизован.
     *
     * @return 200 - пользователь удалён; 401 - ошибка авторизации.
     */
    public ResponseEntity<ServiceResponse<AbstractResponse>> deleteUser() {
        Person person = findCurrentUser();
        if (person == null) {
            log.error("Unauthorized access");
            return ErrorUtil.unauthorized();
        }
        personRepository.deleteById(person.getId());
        log.info("User with ID {} was deleted", person.getId());
        return ResponseEntity.ok(new ServiceResponse<>(ResponseDataMessage.ok()));
    }

    /**
     * Получить пользователя по id.
     * Пользователь должен существовать в базе.
     *
     * @param personId - ID пользователя, которого надо получить.
     * @return 200 - получение пользователя по указанному идентификатору;
     * 400 - произошла ошибка; 401 - ошибка авторизации.
     */
    public ResponseEntity<ServiceResponse<AbstractResponse>> getUserById(long personId) {
        Person person = personRepository.findById(personId).orElse(null);
        if (person == null) {
            log.error("User with ID = {} not found", personId);
            return ErrorUtil.badRequest(String.format("User with ID = %s not found", personId));
        }
        return ResponseEntity.ok(new ServiceResponse<>(getPersonResponse(person)));
    }

    /**
     * Получение записей на стене пользователя.
     * Пользователь должен сщуествовать в базе.
     * Формируется ответ, содержащий список постов, а также информацию о каждом посте.
     *
     * @param personId    - ID пользователя, со стены которого требуется получить записи.
     * @param offset      - Отступ от начала результирующего списка публикаций.
     * @param itemPerPage - Количество публикаций из результирующего списка, которые представлены для отображения.
     * @return 200 - получение результирующего списка с публикациями на стене пользователя;
     * 400 - произошла ошибка; 401 - ошибка авторизации.
     */
    public ResponseEntity<ServiceResponseDataList<WallPostResponse>> getWallPosts(long personId, int offset, int itemPerPage) {
        Person person = personRepository.findById(personId).orElse(null);
        if (person == null) {
            log.error("User with ID = {} not found", personId);
            return ResponseEntity.badRequest()
                    .body(new ServiceResponseDataList<>("User with ID = " + personId + " not found"));
        }
        List<WallPostResponse> wallPosts = new ArrayList<>();
        List<Post> posts = postService.findAllByPersonId(personId, offset, itemPerPage);
        PersonResponse author = getPersonResponse(person);
        for (Post post : posts) {
            List<CommentResponse> comments = commentService.getCommentsByPostId(post.getId());
            WallPostResponse wallPostResponse = postService.getExistsWallPost(post, author, comments);
            wallPosts.add(wallPostResponse);
        }
        int total = postService.getTotalCountPostsByPersonId(personId);
        return ResponseEntity.ok(new ServiceResponseDataList<>(total, offset, itemPerPage, wallPosts));
    }

    /**
     * Добавление публикации на стену пользователя.
     * Если дата публикации не указана или указана прошедшая дата, то устанавливаем текущий момент времени.
     *
     * @param personId        - ID пользователя, который публикует записи.
     * @param publishDate     - Дата публикации, установленная пользователем.
     * @param wallPostRequest - тело запроса в формате JSON. Содержит данные о новой публикации.
     * @return 200 - запись готова к публикации к назначенному времени; 400 - произошла ошибка;
     */
    public ResponseEntity<ServiceResponse<AbstractResponse>> addWallPost(long personId, Long publishDate, WallPostRequest wallPostRequest) {
        Person person = personRepository.findById(personId).orElse(null);
        if (person == null) {
            log.error("User with ID = {} not found", personId);
            return ErrorUtil.badRequest(String.format("User with ID = %s not found", personId));
        }

        LocalDateTime postTime = publishDate != null ?
                TimeUtil.getLocalDateTimeFromTimestamp(publishDate) :
                TimeUtil.now();

        postTime = TimeUtil.beforeNow(postTime) ? TimeUtil.now() : postTime;

        String title = wallPostRequest.getTitle();
        String text = wallPostRequest.getPostText();
        Post post = postService.addPost(person, title, text, postTime);
        PersonResponse author = getPersonResponse(person);
        WallPostResponse newPost = postService.createNewWallPost(post, author);
        return ResponseEntity.ok(new ServiceResponse<>(newPost));
    }

    /**
     * Поиск пользователей по указанным параметрам.
     * Параметры могут быть указаны в различной комбинации (что-то указано, что-то нет).
     * Также параметры могут отсутствовать вовсе. Тогда выводятся все пользователи в количестве itemPerPage, начиная с offset.
     *
     * @param firstName   - Имя пользователей.
     * @param lastName    - Фамилия пользователей.
     * @param ageFrom     - Минимальный возраст пользователей.
     * @param ageTo       - Максимальный возраст пользователей.
     * @param countryId   - Идентификатор страны пользователей.
     * @param cityId      - Идентификатор города пользователей.
     * @param offset      - Отступ от начала результирующего списка пользователей.
     * @param itemPerPage - Количество пользователей из результирующего списка, которые представлены для отображения.
     * @return 200 - Возврат списка пользователей, подходящих по указанным параметрам.
     */
    public ResponseEntity<ServiceResponseDataList<PersonResponse>> findUsers(
            String firstName, String lastName, Integer ageFrom, Integer ageTo,
            Integer countryId, Integer cityId, Integer offset, Integer itemPerPage
    ) {
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        //TODO: без учёта города и страны. Если логика городов и стран заработает - изменить метод поиска в репозитории
        List<Person> personList = personRepository.searchPersons(firstName, lastName, ageFrom, ageTo, pageable);
        List<PersonResponse> searchResult = personList.stream()
                .map(this::getPersonResponse)
                .collect(Collectors.toList());
        int total = personRepository.getTotalCountUsers();
        return ResponseEntity.ok(new ServiceResponseDataList<>(total, offset, itemPerPage, searchResult));
    }

    /**
     * Изменяет статус блокировки пользователя на противоположный.
     * Пользователь должен сщуествовать в базе.
     *
     * @param personId - идентификатор пользователя.
     */
    public ResponseEntity<ServiceResponse<AbstractResponse>> changeUserLockStatus(long personId) {
        Person person = personRepository.findById(personId).orElse(null);
        if (person == null) {
            log.warn("User with id {} do not exist. Lock status didn't changed", personId);
            return ErrorUtil.badRequest(String.format("User with ID = %s not found", personId));
        }
        person.setBlocked(!person.isBlocked());
        personRepository.saveAndFlush(person);
        log.info("User with id {} changed lock status", person.getId());
        return ResponseEntity.ok(new ServiceResponse<>(ResponseDataMessage.ok()));
    }
}
