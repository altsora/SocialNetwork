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
import sn.api.response.ResponseDataMessage;
import sn.api.requests.IsFriendsRequest;
import sn.api.response.IsFriendResponse;
import sn.api.response.PersonResponse;
import sn.api.response.ServiceResponse;
import sn.api.response.ServiceResponseDataList;
import sn.model.Person;
import sn.service.impl.AccountService;
import sn.service.impl.FriendService;

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

    /**
     * Метод getFriendList. Получить список друзей пользователя. GET запрос /api/v1/friends
     *
     * @param name        - поиск по характерному имени
     * @param offset      - Отступ от начала результирующего списка пользователей.
     * @param itemPerPage - Количество пользователей из результирующего списка, которые представлены для
     *                    отображения.
     * @return 200 - список друзей получен успешно (может быть пустым)
     */

    @GetMapping("/friends")
    public ResponseEntity<ServiceResponseDataList<PersonResponse>> getFriendList(
        @RequestParam(required = false) String name,
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "20") int itemPerPage
    ) {

        List<Person> friendList = friendService
            .getFriendList(accountService.findCurrentUser().getId(), name, offset, itemPerPage);
        return ResponseEntity
            .ok(new ServiceResponseDataList<>(
                friendService.getFriendsCount(accountService.findCurrentUser().getId()), offset, itemPerPage,
                friendList.stream().map(accountService::getPersonResponse).collect(Collectors.toList())));
    }

    /**
     * Метод deleteFriend. Удалить друга из друзей. DELETE запрос /api/v1/friends/{friendId}
     *
     * @param friendId - ID друга для удаления из друзей
     * @return 200 - друг удален, 400 - friendId не дружит с пользователем
     */

    @DeleteMapping("/friends/{friendId}")
    public ResponseEntity<ServiceResponse<ResponseDataMessage>> deleteFriend(
        @PathVariable long friendId
    ) {
        return friendService.deleteFriend(accountService.findCurrentUser().getId(), friendId) ?
            ResponseEntity.ok(new ServiceResponse<>(new ResponseDataMessage("ok"))
            ) :
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ServiceResponse<>("Bad request", new ResponseDataMessage("Service unavailable"))
            );
    }

    /**
     * Метод addFriend. Добавить друга в друзья или отправить запрос на дружбу. POST запрос
     * /api/v1/friends/{friendId}
     *
     * @param friendId - ID друга дла добавления
     * @return 200 - друг добавлен, 400 - пользователя с friendId не существует
     */

    @PostMapping("/friends/{friendId}")
    public ResponseEntity<ServiceResponse<ResponseDataMessage>> addFriend(
        @PathVariable long friendId
    ) {
        return friendService.addFriend(accountService.findCurrentUser().getId(), friendId) ?
            ResponseEntity.ok(new ServiceResponse<>(new ResponseDataMessage("ok"))
            ) :
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ServiceResponse<>("Bad request", new ResponseDataMessage("Service unavailable"))
            );
    }

    /**
     * Метод getFriendRequestList. Получить список заявок в друзья. GET запрос /api/v1/friends/request
     *
     * @param name        - поиск по характерному имени
     * @param offset      - Отступ от начала результирующего списка пользователей.
     * @param itemPerPage - Количество пользователей из результирующего списка, которые представлены для
     *                    отображения.
     * @return 200 - список заявок получен успешно (может быть пустым)
     */

    @GetMapping("/friends/request")
    public ResponseEntity<ServiceResponseDataList<PersonResponse>> getFriendRequestList(
        @RequestParam(required = false) String name,
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "20") int itemPerPage
    ) {
        List<Person> requestList = friendService
            .getFriendRequestList(accountService.findCurrentUser().getId(), name, offset, itemPerPage);

        return ResponseEntity
            .ok(new ServiceResponseDataList<>(
                friendService.getTotalCountOfRequest(accountService.findCurrentUser().getId()), offset,
                itemPerPage,
                requestList.stream().map(accountService::getPersonResponse).collect(Collectors.toList())));
    }

    /**
     * Метод getFriendRecommendationList. Получить спискок рекомендованных друзей. GET запрос
     * /api/v1/friends/recommendations
     *
     * @param offset      - Отступ от начала результирующего списка пользователей.
     * @param itemPerPage - Количество пользователей из результирующего списка, которые представлены для
     *                    отображения.
     * @return 200 - список рекомендаций получен успешно (может быть пустым)
     */

    @GetMapping("/friends/recommendations")
    public ResponseEntity<ServiceResponseDataList<PersonResponse>> getFriendRecommendationList(
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "20") int itemPerPage
    ) {

        List<Person> recommendationList = friendService
            .getFriendRecommendationList(accountService.findCurrentUser().getId(),
                accountService.findCurrentUser().getCity(), offset, itemPerPage);

        List<PersonResponse> responseList = new ArrayList<>();
        recommendationList.forEach(p -> responseList.add(accountService.getPersonResponse(p)));

        int total = friendService.getTotalCountOfRecommendationList(accountService.findCurrentUser().getId(),
            accountService.findCurrentUser().getCity());
        return ResponseEntity
            .ok(new ServiceResponseDataList<>(total, offset, itemPerPage, responseList));
    }

    /**
     * Метод isFriend. Проверка списка id на дружбу с пользвателем POST запрос /api/v1/is/friends"
     *
     * @param request - список id для проверки,
     * @see IsFriendResponse
     * @return 200 - список рекомедаций получен успешно, 400 - если проверяемый список пуст или null
     */

    @PostMapping("/is/friends")
    public ResponseEntity<ServiceResponseDataList<IsFriendResponse>> isFriend(
        @RequestBody IsFriendsRequest request
    ) {
        return (request == null || request.getUserIds().size() == 0) ?
            ResponseEntity.badRequest().body(new ServiceResponseDataList<>("Service unavailable"))
            : ResponseEntity.ok(new ServiceResponseDataList<>(
                friendService.isFriend(accountService.findCurrentUser().getId(), request)));
    }
}