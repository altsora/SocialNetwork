package sn.service.impl;

import org.springframework.stereotype.Service;
import sn.api.requests.IsFriendsRequest;
import sn.api.response.AbstractResponse;
import sn.api.response.ServiceResponse;
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
    public ServiceResponse<AbstractResponse> getFriendRequestList(String name, Integer offset,
        int itemPerPage) {

        //TODO SN-25

        return null;
    }

    @Override
    public ServiceResponse<AbstractResponse> getFriendRecommendationList(Integer offset,
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
