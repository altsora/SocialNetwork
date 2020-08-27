package sn.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sn.api.requests.IsFriendsRequest;
import sn.api.response.IsFriendResponse;
import sn.model.Person;
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
        int itemPerPage) {
        Person currentUser = accountService.findCurrentUser();

        List<Person> friendList;
        if (name == null) {
            friendList = personRepository
                .findFriends(offset, itemPerPage, currentUser.getId());
        } else {
            friendList = personRepository
                .findFriendsByName(offset, itemPerPage, currentUser.getId(), name);
        }

        return friendList;
    }

    public int getFriendsCount(long id) {
        return friendshipRepository.findFriendsByPersonIdCount(id);
    }

    @Override
    public boolean deleteFriend(String id) {

        //TODO SN-25
    return false;
    }

    @Override
    public boolean addFriend(String id) {

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
