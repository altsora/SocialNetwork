package sn.controller;

import java.util.ArrayList;
import java.util.List;
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
import sn.api.ResponseDataMessage;
import sn.api.requests.IsFriendsRequest;
import sn.api.response.IsFriendResponse;
import sn.api.response.PersonResponse;
import sn.api.response.ServiceResponse;
import sn.api.response.ServiceResponseDataList;
import sn.model.Person;
import sn.service.impl.AccountService;
import sn.service.impl.FriendService;
import sn.service.impl.PersonService;

@RestController
public class FriendController {

    @Autowired
    private FriendService friendService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PersonService personService;

    @GetMapping("/friends")
    public ResponseEntity<ServiceResponseDataList<PersonResponse>> getFriendList(
        @RequestParam(required = false) String name,
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "20") int itemPerPage
    ) {
        Person person = accountService.findCurrentUser();
        if (person == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ServiceResponseDataList<>("Unauthorized"));
        }

        List<Person> requestList = friendService.getFriendList(name, offset, itemPerPage, person.getId());

        if (requestList == null) {
            return ResponseEntity.badRequest()
                .body(new ServiceResponseDataList<>("Service unavailable"));
        }

        List<PersonResponse> responseList = new ArrayList<>();
        requestList.forEach(p -> responseList.add(personService.getPersonResponse(p)));

        int total = friendService.getFriendsCount(person.getId());
        return ResponseEntity
            .ok(new ServiceResponseDataList<>(total, offset, itemPerPage, responseList));
    }

    @DeleteMapping("/friends/{id}")
    public ResponseEntity<ServiceResponse<ResponseDataMessage>> deleteFriend(
        @PathVariable long id
    ) {
        Person person = accountService.findCurrentUser();
        if (person == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ServiceResponse<>("Unauthorized",
                    new ResponseDataMessage("User is not authorized")));
        }

        return friendService.deleteFriend(id, person.getId()) ?
            ResponseEntity.ok(new ServiceResponse<>(new ResponseDataMessage("ok"))
            ) :
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ServiceResponse<>("Bad request", new ResponseDataMessage("Service unavailable"))
            );
    }

    @PostMapping("/friends/{id}")
    public ResponseEntity<ServiceResponse<ResponseDataMessage>> addFriend(
        @PathVariable long id
    ) {
        Person person = accountService.findCurrentUser();
        if (person == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ServiceResponse<>("Unauthorized",
                    new ResponseDataMessage("User is not authorized")));
        }

        return friendService.deleteFriend(id, person.getId()) ?
            ResponseEntity.ok(new ServiceResponse<>(new ResponseDataMessage("ok"))
            ) :
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ServiceResponse<>("Bad request", new ResponseDataMessage("Service unavailable"))
            );
    }

    @GetMapping("/friends/request")
    public ResponseEntity<ServiceResponseDataList<PersonResponse>> getFriendRequestList(
        @RequestParam(required = false) String name,
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "20") int itemPerPage
    ) {
        Person person = accountService.findCurrentUser();
        if (person == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ServiceResponseDataList<>("Unauthorized"));
        }

        List<Person> requestList = friendService
            .getFriendRequestList(person.getId(), name, offset, itemPerPage);

        if (requestList == null) {
            return ResponseEntity.badRequest()
                .body(new ServiceResponseDataList<>("Service unavailable"));
        }

        List<PersonResponse> responseList = new ArrayList<>();
        requestList.forEach(p -> responseList.add(personService.getPersonResponse(p)));

        int total = friendService.getTotalCountOfRequest(person.getId());
        return ResponseEntity
            .ok(new ServiceResponseDataList<>(total, offset, itemPerPage, responseList));
    }

    @GetMapping("/friends/recommendations")
    public ResponseEntity<ServiceResponseDataList<PersonResponse>> getFriendRecommendationList(
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "20") int itemPerPage
    ) {
        Person person = accountService.findCurrentUser();
        if (person == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ServiceResponseDataList<>("Unauthorized"));
        }

        List<Person> recommendationList = friendService
            .getFriendRecommendationList(person.getId(), offset, itemPerPage);

        if (recommendationList == null) {
            return ResponseEntity.badRequest()
                .body(new ServiceResponseDataList<>("Service unavailable"));
        }

        List<PersonResponse> responseList = new ArrayList<>();
        recommendationList.forEach(p -> responseList.add(personService.getPersonResponse(p)));

        int total = friendService.getTotalCountOfRecommendationList(person.getId());
        return ResponseEntity
            .ok(new ServiceResponseDataList<>(total, offset, itemPerPage, responseList));
    }

    @PostMapping("/is/friends")
    public ResponseEntity<ServiceResponseDataList<IsFriendResponse>> isFriend(
        @RequestBody IsFriendsRequest request
    ) {
        Person person = accountService.findCurrentUser();
        if (person == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ServiceResponseDataList<>("Unauthorized"));
        }

        List<IsFriendResponse> responseList = friendService.isFriend(person.getId(), request);

        if (responseList == null) {
            return ResponseEntity.badRequest()
                .body(new ServiceResponseDataList<>("Service unavailable"));
        }
        return ResponseEntity.ok(new ServiceResponseDataList<>(responseList));
    }
}
