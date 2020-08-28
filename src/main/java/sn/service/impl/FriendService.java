package sn.service.impl;

import java.util.List;
import java.util.Optional;
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
    public List<Person> getFriendList(String name, int offset,
        int itemPerPage, long currentUserId) {

        List<Person> friendList;
        if (name == null) {
            friendList = personRepository
                .findFriends(offset, itemPerPage, currentUserId);
        } else {
            friendList = personRepository
                .findFriendsByName(offset, itemPerPage, currentUserId, name);
        }

        return friendList;
    }

    public int getFriendsCount(long id) {
        return friendshipRepository.findFriendsByPersonIdCount(id);
    }

    @Override
    public boolean deleteFriend(long id, long currentUserId) {

        Optional<Friendship> friendshipOptional = friendshipRepository
            .findByFriendId(currentUserId, id);
        if (friendshipOptional.isEmpty()) {
            return false;
        } else {
            System.out.println(id);
            Friendship friendship = friendshipOptional.get();
            System.out.println("1 - " + friendship.getStatus());
            friendship.setStatus(FriendshipStatusCode.SUBSCRIBED);
            System.out.println("2 - " + friendship.getStatus());
            friendshipRepository.save(friendship);
            System.out.println("3 - " + friendship.getStatus());
            return true;
        }
    }

    @Override
    public boolean addFriend(String id, long currentUserId) {

        //TODO SN-25
        return false;
    }

    @Override
    public List<Person> getFriendRequestList(long personId, String name, Integer offset,
        int itemPerPage) {

        //TODO SN-25

        return null;
    }

    @Override
    public int getTotalCountOfRequest(long personId) {
        return 0;
    }

    @Override
    public List<Person> getFriendRecommendationList(long personId, Integer offset,
        int itemPerPage) {

        //TODO SN-25

        return null;
    }

    @Override
    public int getTotalCountOfRecommendationList(long peronId) {
        return 0;
    }

    @Override
    public List<IsFriendResponse> isFriend(long personId, IsFriendsRequest request) {

        //TODO SN-25

        return null;
    }
}
