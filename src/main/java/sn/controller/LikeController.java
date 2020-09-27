package sn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.api.requests.LikeRequest;
import sn.api.response.*;
import sn.model.Person;
import sn.model.enums.LikeType;
import sn.service.AccountService;
import sn.service.LikeService;

import java.util.List;

@RestController
public class LikeController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private LikeService likeService;

    @GetMapping("/liked")
    public ResponseEntity<Boolean> userHasLiked(
            @RequestParam(value = "user_id") long personId,
            @RequestParam(value = "item_id") long itemId,
            @RequestParam(value = "type") LikeType type
            ) {

        Boolean likes = likeService.likeExists(personId, itemId, type);
        return ResponseEntity.ok(likes);
    }

    @GetMapping("/likes")
    public ResponseEntity<LikeCountResponse> getLikes(
            @RequestParam(value = "item_id") long itemId,
            @RequestParam(value = "type") LikeType type
    ) {
        List<Long> usersId = likeService.getUsersOfLike(itemId,type);
        return ResponseEntity.ok(new LikeCountResponse(usersId.size(),usersId));
    }

    @PutMapping("/likes")
    public ResponseEntity<LikeCountResponse> putLike(@RequestBody LikeRequest lk) {
        likeService.putLike(accountService.findCurrentUser().getId(), lk.getItemId(), lk.getType());
        List<Long> usersId = likeService.getUsersOfLike(lk.getItemId(),lk.getType());
        return ResponseEntity.ok(new LikeCountResponse(usersId.size(),usersId));
    }


    @DeleteMapping("/likes")
    public ResponseEntity<LikeCountResponse> removeLike(
            @RequestParam(value = "item_id") long itemId,
            @RequestParam(value = "type") LikeType type
    ) {
        likeService.removeLike(accountService.findCurrentUser().getId(), itemId, type);
        List<Long> usersId = likeService.getUsersOfLike(itemId,type);
        return ResponseEntity.ok(new LikeCountResponse(usersId.size(),usersId));
    }
}