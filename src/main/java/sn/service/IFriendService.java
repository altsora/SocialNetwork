package sn.service;

import java.util.List;
import sn.api.requests.IsFriendsRequest;
import sn.api.response.IsFriendResponse;
import sn.model.Person;

public interface IFriendService {

    List<Person> getFriendList(String name, int offset, int itemPerPage, long currentUserId);

    boolean deleteFriend(long id, long currentUserId);

    boolean addFriend(String id, long currentUserId);

    List<Person> getFriendRequestList(long personId, String name, Integer offset,
      int itemPerPage);

    int getTotalCountOfRequest(long personId);

    List<Person> getFriendRecommendationList(long personId, Integer offset, int itemPerPage);

    int getTotalCountOfRecommendationList(long peronId);

    List<IsFriendResponse> isFriend(long personId, IsFriendsRequest request);

}
