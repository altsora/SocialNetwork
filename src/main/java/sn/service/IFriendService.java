package sn.service;

import sn.api.requests.IsFriendsRequest;
import sn.api.response.AbstractResponse;
import sn.api.response.ServiceResponse;

public interface IFriendService {

    ServiceResponse<AbstractResponse> getFriendList(String name, Integer offset, int itemPerPage);

    ServiceResponse<AbstractResponse> deleteFriend(String id);

    ServiceResponse<AbstractResponse> addFriend(String id);

    ServiceResponse<AbstractResponse> getFriendRequestList(String name, Integer offset,
        int itemPerPage);

    ServiceResponse<AbstractResponse> getFriendRecommendationList(Integer offset, int itemPerPage);

    ServiceResponse<AbstractResponse> isFriend(IsFriendsRequest request);

}
