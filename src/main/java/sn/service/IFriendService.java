package sn.service;

import java.util.List;
import sn.api.requests.IsFriendsRequest;
import sn.api.response.AbstractResponse;
import sn.api.response.IsFriendResponse;
import sn.api.response.ServiceResponse;
import sn.model.Person;
import sn.api.response.ServiceResponseDataList;

public interface IFriendService {

    ServiceResponseDataList<AbstractResponse> getFriendList(String name, int offset, int itemPerPage);

    ServiceResponse<AbstractResponse> deleteFriend(String id);

    ServiceResponse<AbstractResponse> addFriend(String id);

    List<Person> getFriendRequestList(long personId, String name, Integer offset,
      int itemPerPage);

    int getTotalCountOfRequest(long personId);

    List<Person> getFriendRecommendationList(long personId, Integer offset, int itemPerPage);

    int getTotalCountOfRecommendationList(long peronId);

    List<IsFriendResponse> isFriend(long personId, IsFriendsRequest request);

}
