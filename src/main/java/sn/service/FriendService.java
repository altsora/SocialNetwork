package sn.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sn.api.requests.IsFriendsRequest;
import sn.api.response.IsFriendResponse;
import sn.model.Friendship;
import sn.model.Person;
import sn.model.enums.FriendshipStatusCode;
import sn.repositories.FriendshipRepository;
import sn.repositories.PersonRepository;

/**
 * Класс FriendService Сервисный слой для друзей пользователя.
 */

@Service
public class FriendService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private PersonRepository personRepository;

    /**
     * Метод getFriendList. Получить список друзей
     *
     * @param id          - пользователь
     * @param name        - характерное имя для поиска
     * @param offset      - Отступ от начала результирующего списка пользователей.
     * @param itemPerPage - Количество пользователей из результирующего списка, которые представлены для
     *                    отображения.
     * @return список друзей или пустой список, если друзей не обнаружено
     */

    public List<Person> getFriendList(long id, String name, int offset,
        int itemPerPage) {
        return personRepository.findFriends(id, offset, itemPerPage, (name == null) ? "" : name);
    }

    /**
     * Метод getFriendsCount. Количество друзей пользователя
     *
     * @param id - пользователь
     */

    public int getFriendsCount(long id) {
        return friendshipRepository.getFriendsCount(id);
    }


    /**
     * Метод deleteFriend. Удалить друга, либо сделать его подписчиком
     *
     * @param id       - пользователь
     * @param friendId - друг
     * @return false - если friendId не дружит с пользователь, иначе - true
     */

    public boolean deleteFriend(long id, long friendId) {
        Friendship friendship = friendshipRepository
            .getFriendship(id, friendId, FriendshipStatusCode.FRIEND.toString());
        if (friendship == null) {
            return false;
        }

        if (friendship.getSrcPerson() == id) {
            friendshipRepository.delete(friendship);
        } else {
            friendship.setStatus(FriendshipStatusCode.SUBSCRIBED);
            friendshipRepository.save(friendship);
        }
        return true;
    }

    /**
     * Метод addFriend. Добавить друга, либо отправить запрос на дружбу
     *
     * @param id       - пользователь
     * @param friendId - друг
     * @return false - если пользователь с friendId не существует, иначе - true
     */

    public boolean addFriend(long id, long friendId) {
        if (personRepository.findById(friendId).isEmpty()) {
            return false;
        }

        Friendship friendship = friendshipRepository
            .getFriendship(id, friendId, FriendshipStatusCode.REQUEST.toString());

        if (friendship == null) {
            friendship = new Friendship(id, friendId, FriendshipStatusCode.REQUEST);
        } else {
            friendship.setStatus(FriendshipStatusCode.FRIEND);
        }
        friendshipRepository.save(friendship);
        return true;
    }

    /**
     * Метод getFriendRequestList. Получить список заявок на дружбу
     *
     * @param id          - пользователь
     * @param name        - характерное имя для поиска
     * @param offset      - Отступ от начала результирующего списка пользователей.
     * @param itemPerPage - Количество пользователей из результирующего списка, которые представлены для
     *                    отображения.
     * @return список заявок или пустой список, если заявок не обнаружено
     */

    public List<Person> getFriendRequestList(long id, String name, Integer offset,
        int itemPerPage) {

        return personRepository.findRequests(id, offset, itemPerPage, (name == null) ? "" : name);
    }

    /**
     * Метод getTotalCountOfRequest. Количество заявок на дружбу
     *
     * @param id - пользователь
     */

    public int getTotalCountOfRequest(long id) {
        return friendshipRepository.getRequestsCount(id);
    }

    /**
     * Метод getFriendRecommendationList. Получить список рекомендованных друзей
     *
     * @param id          - пользователь
     * @param city        - город пользователя
     * @param offset      - Отступ от начала результирующего списка пользователей.
     * @param itemPerPage - Количество пользователей из результирующего списка, которые представлены для
     *                    отображения.
     * @return список рекомендованных друзей или пустой список, заявок некого рекомендовать
     */

    public List<Person> getFriendRecommendationList(long id, String city, Integer offset,
        int itemPerPage) {
        return personRepository.findRecommendedFriends(id, city, offset, itemPerPage);
    }

    /**
     * Метод getTotalCountOfRecommendationList. Количество возможных рекомендованных друзей
     *
     * @param id   - пользователь
     * @param city - город пользователя
     */

    public int getTotalCountOfRecommendationList(long id, String city) {
        return 0;
    }

    /**
     * Метод isFriend. Проверка списка id на дружбу с пользвателем
     *
     * @param id      - пользователь
     * @param request - список id для проверки, @see IsFriendResponse
     * @return список только тех id, которые дружат с пользователем
     * @see IsFriendResponse
     */

    public List<IsFriendResponse> isFriend(long id, IsFriendsRequest request) {
        List<Long> friends = getFriendList(id, null, 0, getFriendsCount(id)).stream().map(Person::getId).collect(
            Collectors.toList());
        friends.retainAll(request.getUserIds());
        return friends.stream().map(f -> new IsFriendResponse(f, FriendshipStatusCode.FRIEND))
            .collect(Collectors.toList());
    }
}

