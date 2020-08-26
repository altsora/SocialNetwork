package sn.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import sn.api.requests.IsFriendsRequest;
import sn.api.response.AbstractResponse;
import sn.api.response.IsFriendResponse;
import sn.api.response.ServiceResponse;
import sn.model.Person;
import sn.service.IFriendService;

@Service
public class FriendService implements IFriendService {


    @Override
    public ServiceResponse<AbstractResponse> getFriendList(String name, Integer offset,
        int itemPerPage) {

        //TODO SN-25

        return null;
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
    public List<IsFriendResponse> isFriend(long personId,IsFriendsRequest request) {

        //TODO SN-25

        return null;
    }
}
