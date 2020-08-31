package sn.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sn.api.ResponseDataMessage;
import sn.api.requests.IsFriendsRequest;
import sn.api.response.IsFriendResponse;
import sn.api.response.PersonResponse;
import sn.api.response.ServiceResponse;
import sn.api.response.ServiceResponseDataList;
import sn.model.Person;
import sn.service.impl.AccountService;
import sn.service.impl.FriendService;
import sn.service.impl.PersonService;

/**
 * Класс FriendController.
 *
 * REST-контроллер для работы с друзьями.
 */

@RestController
public class FriendController {

    @Autowired
    private FriendService friendService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PersonService personService;

    /**
     * Метод getFriendList. Получить список друзей пользователя.
     * GET запрос /api/v1/friends
     *
     * @param name        - поиск по характерному имени
     * @param offset      - Отступ от начала результирующего списка пользователей.
     * @param itemPerPage - Количество пользователей из результирующего списка, которые представлены для
     *                    отображения.
     * @return 200 список друзей получен успешно (может быть пустым), 401 - пользователь не авторизирован
     */

    @GetMapping("/friends")
    public ResponseEntity<ServiceResponseDataList<PersonResponse>> getFriendList(
        @RequestParam(required = false) String name,
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "20") int itemPerPage
    ) {
        Person person = accountService.findCurrentUser();
        if (person == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ServiceResponseDataList<>("Unauthorized"));
        }

        List<Person> friendList = friendService.getFriendList(person.getId(), name, offset, itemPerPage);

        return ResponseEntity
            .ok(new ServiceResponseDataList<>(friendService.getFriendsCount(person.getId()), offset, itemPerPage,
                friendList.stream().map(personService::getPersonResponse).collect(Collectors.toList())));
    }

    /**
     * Метод deleteFriend. Удалить друга из друзей.
     * DELETE запрос /api/v1/friends/{friendId}
     *
     * @param friendId - ID друга для удаления из друзей
     * @return 200 друг удален, 401 - пользователь не авторизирован, 400 - friendId не дружит с пользователем
     */

    @DeleteMapping("/friends/{friendId}")
    public ResponseEntity<ServiceResponse<ResponseDataMessage>> deleteFriend(
        @PathVariable long friendId
    ) {
        Person person = accountService.findCurrentUser();
        if (person == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ServiceResponse<>("Unauthorized",
                    new ResponseDataMessage("User is not authorized")));
        }

        return friendService.deleteFriend(person.getId(), friendId) ?
            ResponseEntity.ok(new ServiceResponse<>(new ResponseDataMessage("ok"))
            ) :
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ServiceResponse<>("Bad request", new ResponseDataMessage("Service unavailable"))
            );
    }

    /**
     * Метод addFriend. Добавить друга в друзья или отправить запрос на дружбу.
     * POST запрос /api/v1/friends/{friendId}
     *
     * @param friendId - ID друга дла добавления
     * @return 200 друг добавлен, 401 - пользователь не авторизирован, 400 - пользователя с friendId не существует
     */

    @PostMapping("/friends/{friendId}")
    public ResponseEntity<ServiceResponse<ResponseDataMessage>> addFriend(
        @PathVariable long friendId
    ) {
        Person person = accountService.findCurrentUser();
        if (person == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ServiceResponse<>("Unauthorized",
                    new ResponseDataMessage("User is not authorized")));
        }
        return friendService.addFriend(person.getId(), friendId) ?
            ResponseEntity.ok(new ServiceResponse<>(new ResponseDataMessage("ok"))
            ) :
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ServiceResponse<>("Bad request", new ResponseDataMessage("Service unavailable"))
            );
    }

    /**
     * Метод getFriendRequestList. Получить список заявок в друзья.
     * GET запрос /api/v1/friends/request
     *
     * @param name        - поиск по характерному имени
     * @param offset      - Отступ от начала результирующего списка пользователей.
     * @param itemPerPage - Количество пользователей из результирующего списка, которые представлены для
     *                    отображения.
     * @return 200 список заявок получен успешно (может быть пустым), 401 - пользователь не авторизирован
     */

    @GetMapping("/friends/request")
    public ResponseEntity<ServiceResponseDataList<PersonResponse>> getFriendRequestList(
        @RequestParam(required = false) String name,
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "20") int itemPerPage
    ) {
        Person person = accountService.findCurrentUser();
        if (person == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ServiceResponseDataList<>("Unauthorized"));
        }

        List<Person> requestList = friendService
            .getFriendRequestList(person.getId(), name, offset, itemPerPage);

        return ResponseEntity
            .ok(new ServiceResponseDataList<>(friendService.getTotalCountOfRequest(person.getId()), offset,
                itemPerPage,
                requestList.stream().map(personService::getPersonResponse).collect(Collectors.toList())));
    }

    /**
     * Метод getFriendRecommendationList. Получить спискок рекомендованных друзей.
     * GET запрос /api/v1/friends/recommendations
     *
     * @param offset      - Отступ от начала результирующего списка пользователей.
     * @param itemPerPage - Количество пользователей из результирующего списка, которые представлены для
     *                    отображения.
     * @return 200 список рекомендаций получен успешно (может быть пустым), 401 - пользователь не авторизирован
     */

    @GetMapping("/friends/recommendations")
    public ResponseEntity<ServiceResponseDataList<PersonResponse>> getFriendRecommendationList(
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "20") int itemPerPage
    ) {
        Person person = accountService.findCurrentUser();
        if (person == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ServiceResponseDataList<>("Unauthorized"));
        }

        List<Person> recommendationList = friendService
            .getFriendRecommendationList(person.getId(), person.getCity(), offset, itemPerPage);

        List<PersonResponse> responseList = new ArrayList<>();
        recommendationList.forEach(p -> responseList.add(personService.getPersonResponse(p)));

        int total = friendService.getTotalCountOfRecommendationList(person.getId(), person.getCity());
        return ResponseEntity
            .ok(new ServiceResponseDataList<>(total, offset, itemPerPage, responseList));
    }

    /**
     * Метод isFriend. Проверка списка id на дружбу с пользвателем
     * POST запрос /api/v1/is/friends"
     *
     * @param request - список id для проверки, @see IsFriendResponse
     *
     * @return 200 список рекомедаций получен успешно, 401 - пользователь не авторизирован, 400 - если проверяемый
     * список пуст или null
     */

    @PostMapping("/is/friends")
    public ResponseEntity<ServiceResponseDataList<IsFriendResponse>> isFriend(
        @RequestBody IsFriendsRequest request
    ) {
        Person person = accountService.findCurrentUser();
        if (person == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ServiceResponseDataList<>("Unauthorized"));
        }

        if (request == null || request.getUserIds().size() == 0) {
            return ResponseEntity.badRequest()
                .body(new ServiceResponseDataList<>("Service unavailable"));
        }
        return ResponseEntity.ok(new ServiceResponseDataList<>(friendService.isFriend(person.getId(), request)));
    }
}