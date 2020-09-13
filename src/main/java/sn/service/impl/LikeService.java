package sn.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sn.model.CommentLike;
import sn.model.PostLike;
import sn.repositories.CommentLikeRepository;
import sn.repositories.PersonRepository;
import sn.repositories.PostLikeRepository;
import sn.service.ILikeService;
//import sn.service.IPersonService;
import sn.service.IPostService;
import sn.utils.TimeUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService implements ILikeService {
//    private final IPersonService personService;
    private final IPostService postService;
    private final PersonRepository personRepository;
    private final PostLikeRepository postLikeRepository;

    public static final String COMMENT_LIKE = "Comment";
    public static final String POST_LIKE = "Post";

    //==================================================================================================================

    /**
     * Проверка, существует ли лайк (под постом или комментарием).
     *
     * @param personId - идентификатор пользователя;
     * @param likeType - тип лайка (под постом или комментарием);
     * @param itemId   - идентификатор объекта, у которого проверяется наличие лайка.
     * @return - возвращает true, если лайк стоит, иначе false.
     */
    @Override
    public boolean likeExists(long personId, String likeType, long itemId) {
        switch (likeType) {
            case COMMENT_LIKE:
                return commentLikeRepository.findByPersonIdAndCommentId(personId, itemId) != null;
            case POST_LIKE:
                return postLikeRepository.findByPersonIdAndPostId(personId, itemId) != null;
            default:
                return false;
        }
    }

    /**
     * Метод возвращает количество лайков под объектом.
     *
     * @param likeType - тип лайка (под постом или комментарием);
     * @param itemId   - идентификатор объекта, у которого проверяется количество лайков.
     * @return - возвращает количество лайков у объекта.
     */
    @Override
    public int getCount(String likeType, long itemId) {
        switch (likeType) {
            case COMMENT_LIKE:
                return commentLikeRepository.getCount(itemId);
            case POST_LIKE:
                return postLikeRepository.getCount(itemId);
            default:
                return 0;
        }
    }

    /**
     * Метод список пользователей (их идентификаторы), которые поставили лайк под объектом.
     *
     * @param likeType - тип лайка (под постом или комментарием);
     * @param itemId   - идентификатор объекта, у которого стоит лайк.
     * @return - возвращает список ID пользователей, поставивших лайк.
     */
    @Override
    public List<Long> getUsersOfLike(String likeType, long itemId) {
        List<Long> users = new ArrayList<>();
        switch (likeType) {
            case COMMENT_LIKE:
                commentLikeRepository.findAllByCommentId(itemId)
                        .forEach(commentLike -> users.add(commentLike.getId()));
                return users;
            case POST_LIKE:
                postLikeRepository.findAllByPostId(itemId)
                        .forEach(postLike -> users.add(postLike.getId()));
                return users;
            default:
                return users;
        }
    }

//    @Override
//    public void putLike(long personId, LikeType likeType, long itemId) {
//        Like like = new Like();
//        like.setItemId(itemId);
//        like.setLikeType(likeType);
//        like.setTime(LocalDateTime.now(TimeUtil.TIME_ZONE));
//        like.setPerson(personService.findById(personId));
//        likeRepository.saveAndFlush(like);
//        if (likeType == LikeType.POST) {
//            postService.putLike(itemId);
//        }
//    }

    /**
     * Метод удаляет лайк из базы.
     *
     * @param personId - идентификатор пользователя;
     * @param likeType - тип лайка (под постом или комментарием);
     * @param itemId   - идентификатор объекта, которому ставится лайк.
     */
    @Override
    public void removeLike(long personId, String likeType, long itemId) {
        switch (likeType) {
            case COMMENT_LIKE:
                removeCommentLike(personId, itemId);
                return;
            case POST_LIKE:
                removePostLike(personId, itemId);
        }
    }

//    @Override
//    public void removeLike(long personId, LikeType likeType, long itemId) {
//        Like like = likeRepository.findLike(personId, likeType, itemId);
//        likeRepository.deleteById(like.getId());
//        if (likeType == LikeType.POST) {
//            postService.removeLike(itemId);
//        }
//    }
}
