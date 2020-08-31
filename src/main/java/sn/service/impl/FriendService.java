package sn.service.impl;

import java.util.ArrayList;
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
import sn.service.IFriendService;

@Service
public class FriendService implements IFriendService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private PersonRepository personRepository;

    @Override
    public List<Person> getFriendList(long id, String name, int offset,
        int itemPerPage) {
        return personRepository.findFriends(id, offset, itemPerPage, (name == null) ? "" : name);
    }

    public int getFriendsCount(long id) {
        return friendshipRepository.getFriendsCount(id);
    }

    @Override
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

    @Override
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

    @Override
    public List<Person> getFriendRequestList(long id, String name, Integer offset,
        int itemPerPage) {

        return personRepository.findRequests(id, offset, itemPerPage, (name == null) ? "" : name);
    }

    @Override
    public int getTotalCountOfRequest(long id) {
        return friendshipRepository.getRequestsCount(id);
    }

    @Override
    public List<Person> getFriendRecommendationList(long id, String city, Integer offset,
        int itemPerPage) {

        return personRepository.findRecommendedFriends(city, offset, itemPerPage);

    }

    @Override
    public int getTotalCountOfRecommendationList(long id, String city) {
        return 0;
    }

    @Override
    public List<IsFriendResponse> isFriend(long id, IsFriendsRequest request) {
        List<Long> friends = getFriendList(id, null, 0, getFriendsCount(id)).stream().map(Person::getId).collect(
            Collectors.toList());
        friends.retainAll(request.getUserIds());
        return friends.stream().map(f -> new IsFriendResponse(f, FriendshipStatusCode.FRIEND))
            .collect(Collectors.toList());
    }
}
