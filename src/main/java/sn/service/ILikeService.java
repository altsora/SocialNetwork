package sn.service;

import sn.model.enums.LikeType;

import java.util.List;

/**
 * Интерфейс ILikeService.
 * Методы для работы с классом Like.
 *
 * @see sn.model.Like
 */
public interface ILikeService {
    boolean likeExists(long personId, LikeType likeType, long itemId);

    int getCount(LikeType likeType, long itemId);

    List<Long> getUsersOfLike(LikeType likeType, long itemId);

//    void putLike(long personId, LikeType likeType, long itemId);

    LikeType getLikeType(String type);

//    void removeLike(long personId, LikeType likeType, long itemId);
}
