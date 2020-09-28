package sn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.api.requests.LikeRequest;
import sn.api.response.*;
import sn.model.Person;
import sn.model.enums.LikeType;
import sn.repositories.PersonRepository;
import sn.service.AccountService;
import sn.service.LikeService;

import java.util.List;
import java.util.Optional;

@RestController
public class LikeController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private PersonRepository personRepository;

    @GetMapping("/liked")
    public ResponseEntity<Boolean> userHasLiked(
            @RequestParam(value = "user_id") long personId,
            @RequestParam(value = "item_id") long itemId,
            @RequestParam(value = "type") LikeType type
    ) {

        Person person = personRepository.findById(personId).orElse(null);
        if (person == null) {
            return ResponseEntity.badRequest().body(false);
        }
        Boolean likes = likeService.likeExists(person, itemId, type);
        return ResponseEntity.ok(likes);
    }

    @GetMapping("/likes")
    public ResponseEntity<LikeCountResponse> getLikes(
            @RequestParam(value = "item_id") long itemId,
            @RequestParam(value = "type") LikeType type
    ) {
        List<Long> usersId = likeService.getUsersOfLike(itemId, type);
        return ResponseEntity.ok(new LikeCountResponse(usersId.size(), usersId));
    }

    @PutMapping("/likes")
    public ResponseEntity<?> putLike(@RequestBody LikeRequest lk) {
        Boolean result = likeService.putLike(accountService.findCurrentUser(), lk.getItemId(), lk.getType());
        if (result) {
            List<Long> usersId = likeService.getUsersOfLike(lk.getItemId(), lk.getType());
            return ResponseEntity.ok(new LikeCountResponse(usersId.size(), usersId));
        } else {
            return ResponseEntity.badRequest().body("User have like on this item");
        }
    }


    @DeleteMapping("/likes")
    public ResponseEntity<LikeCountResponse> removeLike(
            @RequestParam(value = "item_id") long itemId,
            @RequestParam(value = "type") LikeType type
    ) {
        likeService.removeLike(accountService.findCurrentUser(), itemId, type);
        List<Long> usersId = likeService.getUsersOfLike(itemId, type);
        return ResponseEntity.ok(new LikeCountResponse(usersId.size(), usersId));
    }
}