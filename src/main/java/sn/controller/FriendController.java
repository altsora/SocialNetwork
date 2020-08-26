package sn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sn.api.requests.IsFriendsRequest;
import sn.api.response.AbstractResponse;
import sn.api.response.ServiceResponse;
import sn.api.response.ServiceResponseDataList;
import sn.service.impl.FriendService;

@RestController
public class FriendController {

    @Autowired
    private FriendService friendService;

    @GetMapping("/friends")
    public ResponseEntity<ServiceResponseDataList<AbstractResponse>> getFriendList(
        @RequestParam(required = false) String name,
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "20") int itemPerPage
    ) {

        //TODO authorization

        return new ResponseEntity<>(friendService.getFriendList(name, offset, itemPerPage), HttpStatus.OK);
    }

    @DeleteMapping("/friends/{id}")
    public ResponseEntity<ServiceResponse<AbstractResponse>> deleteFriend(
        @PathVariable String id
    ) {
        return new ResponseEntity<>(friendService.deleteFriend(id),
            HttpStatus.OK);
    }

    @PostMapping("/friends/{id}")
    public ResponseEntity<ServiceResponse<AbstractResponse>> addFriend(
        @PathVariable String id
    ) {
        return new ResponseEntity<>(friendService.addFriend(id),
            HttpStatus.OK);
    }

    @GetMapping("/friends/request")
    public ResponseEntity<ServiceResponse<AbstractResponse>> getFriendRequestList(
        @RequestParam(required = false) String name,
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "20") int itemPerPage
    ) {
        return new ResponseEntity<>(
            friendService.getFriendRequestList(name, offset, itemPerPage), HttpStatus.OK);
    }

    @GetMapping("/friends/recommendations")
    public ResponseEntity<ServiceResponse<AbstractResponse>> getFriendRecommendationList(
        @RequestParam(required = false) Integer offset,
        @RequestParam(defaultValue = "20") int itemPerPage
    ) {
        return new ResponseEntity<>(
            friendService.getFriendRecommendationList(offset, itemPerPage), HttpStatus.OK);
    }

    @PostMapping("/is/friends")
    public ResponseEntity<ServiceResponse<AbstractResponse>> isFriend(
        @RequestBody IsFriendsRequest request
    ) {
        request.getUserIds().forEach(System.out::println);
        return new ResponseEntity<>(
            friendService.isFriend(request), HttpStatus.OK);
    }
}
