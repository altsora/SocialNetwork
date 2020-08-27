package sn.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.api.requests.LikeRequest;
import sn.api.response.*;
import sn.model.enums.LikeType;
import sn.service.IAccountService;
import sn.service.ILikeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LikeController {
    private final IAccountService accountService;
    private final ILikeService likeService;

    //==================================================================================================================

    @GetMapping("/liked")
    public ResponseEntity<ServiceResponse<AbstractResponse>> userHasLiked(
            @RequestParam(value = "user_id") long personId,
            @RequestParam(value = "item_id") long itemId,
            @RequestParam(value = "type") String type
    ) {
        boolean value;
        switch (type) {
            case "Post":
                value = likeService.likeExists(personId, LikeType.POST, itemId);
                break;
            case "Comment":
                value = likeService.likeExists(personId, LikeType.COMMENT, itemId);
                break;
            default:
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .error("Bad request")
                        .errorDescription("Unknown type: " + type)
                        .build();
                return ResponseEntity.badRequest().body(new ServiceResponse<>(errorResponse));
        }
        LikeValueResponse likeValueResponse = LikeValueResponse.builder().likeValue(value).build();
        return ResponseEntity.ok(new ServiceResponse<>(likeValueResponse));
    }

    @GetMapping("/likes")
    public ResponseEntity<ServiceResponse<AbstractResponse>> getLikes(
            @RequestParam(value = "item_id") long itemId,
            @RequestParam(value = "type") String type
    ) {
        LikeType likeType = likeService.getLikeType(type);
        if (likeType == null) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .error("Bad request")
                    .errorDescription("Unknown type: " + type)
                    .build();
            return ResponseEntity.badRequest().body(new ServiceResponse<>(errorResponse));
        }
        int likes = likeService.getCount(likeType, itemId);
        List<Long> users = likeService.getUsersOfLike(likeType, itemId);
        return ResponseEntity.ok(new ServiceResponse<>(new LikeCountResponse(likes, users)));
    }

    @PutMapping("/likes")
    public ResponseEntity<ServiceResponse<AbstractResponse>> putLike(@RequestBody LikeRequest likeRequest) {
        long personId = accountService.findCurrentUser().getId();
        long itemId = likeRequest.getItemId();
        LikeType likeType = likeService.getLikeType(likeRequest.getType());
        if (likeType == null) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .error("Bad request")
                    .errorDescription("Unknown type: " + likeRequest.getType())
                    .build();
            return ResponseEntity.badRequest().body(new ServiceResponse<>(errorResponse));
        }
        likeService.putLike(personId, likeType, likeRequest.getItemId());
        int likes = likeService.getCount(likeType, itemId);
        List<Long> users = likeService.getUsersOfLike(likeType, itemId);
        return ResponseEntity.ok(new ServiceResponse<>(new LikeCountResponse(likes, users)));
    }

    @DeleteMapping("/likes")
    public ResponseEntity<ServiceResponse<AbstractResponse>> removeLike(
            @RequestParam(value = "item_id") long itemId,
            @RequestParam(value = "type") String type
    ) {
        long personId = accountService.findCurrentUser().getId();
        LikeType likeType = likeService.getLikeType(type);
        if (likeType == null) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .error("Bad request")
                    .errorDescription("Unknown type: " + type)
                    .build();
            return ResponseEntity.badRequest().body(new ServiceResponse<>(errorResponse));
        }
        likeService.removeLike(personId, likeType, itemId);
        int likes = likeService.getCount(likeType, itemId);
        return ResponseEntity.ok(new ServiceResponse<>(new LikeCountResponse(likes)));
    }
}
