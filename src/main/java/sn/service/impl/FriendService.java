package sn.service.impl;

import java.util.List;
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

        return name == null ? personRepository.findFriends(id, offset, itemPerPage)
            : personRepository.findFriendsByName(id, name, offset, itemPerPage);
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

        //TODO SN-25

        return null;
    }

    @Override
    public int getTotalCountOfRequest(long id) {
        return 0;
    }

    @Override
    public List<Person> getFriendRecommendationList(long personId, Integer offset,
        int itemPerPage) {

        //TODO SN-25

        return null;
    }

    @Override
    public int getTotalCountOfRecommendationList(long id) {
        return 0;
    }

    @Override
    public List<IsFriendResponse> isFriend(long id, IsFriendsRequest request) {

        //TODO SN-25

        return null;
    }
}
