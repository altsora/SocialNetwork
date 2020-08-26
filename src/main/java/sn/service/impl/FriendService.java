package sn.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sn.api.requests.IsFriendsRequest;
import sn.api.response.AbstractResponse;
import sn.api.response.PersonResponse;
import sn.api.response.ServiceResponse;
import sn.api.response.ServiceResponseDataList;
import sn.model.Friendship;
import sn.model.Person;
import sn.repositories.FriendshipRepository;
import sn.service.IFriendService;

@Service
public class FriendService implements IFriendService {

    @Autowired
    private PersonService personService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Override
    public ServiceResponseDataList<AbstractResponse> getFriendList(String name, int offset,
        int itemPerPage) {
        Person currentUser = accountService.findCurrentUser();

        int total = getFriendsCount(currentUser.getId());
        List<PersonResponse> friendList = new ArrayList<>();

        List<Friendship> friendshipList = getFriendshipListByPersonId(name,
            offset, itemPerPage, currentUser.getId());
        System.out.println(name);
        System.out.println(offset);
        System.out.println(itemPerPage);
        System.out.println(currentUser.getId());

        for (Friendship friendship : friendshipList) {
            long srcPersonId = friendship.getSrcPerson().getId();
            long dstPersonId = friendship.getDstPerson().getId();
            Person friend;
            if (srcPersonId == currentUser.getId()) {
                friend = personService.findById(dstPersonId);
            } else {
                friend = personService.findById(srcPersonId);
            }
            PersonResponse personResponse = personService.getPersonResponse(friend);
            friendList.add(personResponse);
        }

        ServiceResponseDataList response = new ServiceResponseDataList(total, offset, itemPerPage,
            friendList);
        return response;
    }

    private List<Friendship> getFriendshipListByPersonId(String name, int offset, int itemPerPage,
        long id) {
        Iterable<Friendship> friendshipIterable = friendshipRepository
            .findFriendshipByPersonId(offset, itemPerPage, id);
        List<Friendship> friendshipList = new ArrayList<>();
        for (Friendship friendship : friendshipIterable) {
            friendshipList.add(friendship);
            System.out.println(friendship.getId());
        }

        if (name != null) {
            friendshipList = getFriendListByPersonName(name, friendshipList);
        }

        return friendshipList;
    }

    private List<Friendship> getFriendListByPersonName(String name,
        List<Friendship> friendshipList) {

        Iterable<Person> personIterable1 = personService
            .searchPersons(name, null, null, null, null, null);

        Iterable<Person> personIterable2 = personService
            .searchPersons(null, name, null, null, null, null);

        Set<Person> personSet = new HashSet<>();
        for (Person person : personIterable1) {
            personSet.add(person);
        }
        for (Person person : personIterable2) {
            personSet.add(person);
        }

        for (Friendship friendship : friendshipList) {
            for (Person person : personSet) {
                long personId = person.getId();
                long srcPersonId = friendship.getSrcPerson().getId();
                long dstPersonId = friendship.getDstPerson().getId();
                if (srcPersonId == personId || dstPersonId == personId) {
                    friendshipList.remove(friendship);
                }
            }
        }
        return friendshipList;
    }

    private int getFriendsCount(long id) {
        return friendshipRepository.findFriendsByPersonIdCount(id);
    }

    @Override
    public ServiceResponse<AbstractResponse> deleteFriend(String id) {

        //TODO SN-25

        return null;
    }

    @Override
    public ServiceResponse<AbstractResponse> addFriend(String id) {

        //TODO SN-25

        return null;
    }

    @Override
    public ServiceResponse<AbstractResponse> getFriendRequestList(String name, int offset,
        int itemPerPage) {

        //TODO SN-25

        return null;
    }

    @Override
    public ServiceResponse<AbstractResponse> getFriendRecommendationList(int offset,
        int itemPerPage) {

        //TODO SN-25

        return null;
    }

    @Override
    public ServiceResponse<AbstractResponse> isFriend(IsFriendsRequest request) {

        //TODO SN-25

        return null;
    }
}
