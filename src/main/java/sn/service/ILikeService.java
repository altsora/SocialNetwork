package sn.service;

import java.util.List;

/**
 * Интерфейс ILikeService.
 * Методы для работы с лайками постов и лайками комментариев.
 */
public interface ILikeService {
    boolean likeExists(long personId, String likeType, long itemId);

    int getCount(String likeType, long itemId);

    List<Long> getUsersOfLike(String likeType, long itemId);

    void putLike(long personId, String likeType, long itemId);

    void removeLike(long personId, String likeType, long itemId);
}
