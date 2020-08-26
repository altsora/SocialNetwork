package sn.service;

import sn.api.requests.IsFriendsRequest;
import sn.api.response.AbstractResponse;
import sn.api.response.ServiceResponse;
import sn.api.response.ServiceResponseDataList;

public interface IFriendService {

    ServiceResponseDataList<AbstractResponse> getFriendList(String name, int offset, int itemPerPage);

    ServiceResponse<AbstractResponse> deleteFriend(String id);

    ServiceResponse<AbstractResponse> addFriend(String id);

    ServiceResponse<AbstractResponse> getFriendRequestList(String name, int offset,
        int itemPerPage);

    ServiceResponse<AbstractResponse> getFriendRecommendationList(int offset, int itemPerPage);

    ServiceResponse<AbstractResponse> isFriend(IsFriendsRequest request);

}
