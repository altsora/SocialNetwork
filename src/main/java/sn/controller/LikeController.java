package sn.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.api.requests.LikeRequest;
import sn.api.response.*;
import sn.service.IAccountService;
import sn.service.ILikeService;

import java.util.List;

@RestController
public class LikeController {
    private final IAccountService accountService;
    private final ILikeService likeService;

    private final String COMMENT_LIKE = "Comment";
    private final String POST_LIKE = "Post";

    public LikeController(
            @Qualifier("account-service") IAccountService accountService,
            ILikeService likeService) {
        this.accountService = accountService;
        this.likeService = likeService;
    }

    //==================================================================================================================

    @GetMapping("/liked")
    public ResponseEntity<ServiceResponse<AbstractResponse>> userHasLiked(
            @RequestParam(value = "user_id") long personId,
            @RequestParam(value = "item_id") long itemId,
            @RequestParam(value = "type") String likeType
    ) {
        boolean value;
        switch (likeType) {
            case COMMENT_LIKE:
            case POST_LIKE:
                value = likeService.likeExists(personId, likeType, itemId);
                break;
            default:
                ErrorResponse errorResponse = getErrorUnknownLikeType(likeType);
                return ResponseEntity.badRequest().body(new ServiceResponse<>(errorResponse));
        }
        LikeValueResponse likeValueResponse = LikeValueResponse.builder().likeValue(value).build();
        return ResponseEntity.ok(new ServiceResponse<>(likeValueResponse));
    }

    @GetMapping("/likes")
    public ResponseEntity<ServiceResponse<AbstractResponse>> getLikes(
            @RequestParam(value = "item_id") long itemId,
            @RequestParam(value = "type") String likeType
    ) {
        int likes;
        switch (likeType) {
            case COMMENT_LIKE:
            case POST_LIKE:
                likes = likeService.getCount(likeType, itemId);
                break;
            default:
                ErrorResponse errorResponse = getErrorUnknownLikeType(likeType);
                return ResponseEntity.badRequest().body(new ServiceResponse<>(errorResponse));
        }
        List<Long> users = likeService.getUsersOfLike(likeType, itemId);
        return ResponseEntity.ok(new ServiceResponse<>(new LikeCountResponse(likes, users)));
    }

    @PutMapping("/likes")
    public ResponseEntity<ServiceResponse<AbstractResponse>> putLike(@RequestBody LikeRequest likeRequest) {
        long personId = accountService.findCurrentUser().getId();
        String likeType = likeRequest.getType();
        long itemId = likeRequest.getItemId();

        int likes;
        switch (likeType) {
            case COMMENT_LIKE:
            case POST_LIKE:
                likeService.putLike(personId, likeType, itemId);
                likes = likeService.getCount(likeType, itemId);
                break;
            default:
                ErrorResponse errorResponse = getErrorUnknownLikeType(likeType);
                return ResponseEntity.badRequest().body(new ServiceResponse<>(errorResponse));
        }
        List<Long> users = likeService.getUsersOfLike(likeType, itemId);
        return ResponseEntity.ok(new ServiceResponse<>(new LikeCountResponse(likes, users)));
    }

    @DeleteMapping("/likes")
    public ResponseEntity<ServiceResponse<AbstractResponse>> removeLike(
            @RequestParam(value = "item_id") long itemId,
            @RequestParam(value = "type") String likeType
    ) {
        long personId = accountService.findCurrentUser().getId();
        int likes;
        switch (likeType) {
            case COMMENT_LIKE:
            case POST_LIKE:
                likeService.removeLike(personId, likeType, itemId);
                likes = likeService.getCount(likeType, itemId);
                break;
            default:
                ErrorResponse errorResponse = getErrorUnknownLikeType(likeType);
                return ResponseEntity.badRequest().body(new ServiceResponse<>(errorResponse));
        }
        return ResponseEntity.ok(new ServiceResponse<>(new LikeCountResponse(likes)));
    }

    private ErrorResponse getErrorUnknownLikeType(String likeType) {
        return ErrorResponse.builder()
                .error("Bad request")
                .errorDescription("Unknown type: " + likeType)
                .build();
    }
}