package sn.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sn.model.CommentLike;
import sn.model.PostLike;
import sn.repositories.CommentLikeRepository;
import sn.repositories.PersonRepository;
import sn.repositories.PostLikeRepository;
import sn.utils.TimeUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final CommentService commentService;
    private final PostService postService;
    private final PersonRepository personRepository;

    @Autowired
    public LikeService(CommentLikeRepository commentLikeRepository,
        CommentService commentService, PostService postService,
        PersonRepository personRepository, PostLikeRepository postLikeRepository) {
        this.commentLikeRepository = commentLikeRepository;
        this.commentService = commentService;
        this.postService = postService;
        this.personRepository = personRepository;
        this.postLikeRepository = postLikeRepository;
    }

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

    /**
     * Метод создаёт лайк в базе.
     *
     * @param personId - идентификатор пользователя;
     * @param likeType - тип лайка (под постом или комментарием);
     * @param itemId   - идентификатор объекта, которому ставится лайк.
     */
    public void putLike(long personId, String likeType, long itemId) {
        switch (likeType) {
            case COMMENT_LIKE:
                putCommentLike(personId, itemId);
                return;
            case POST_LIKE:
                putPostLike(personId, itemId);
        }
    }

    /**
     * Метод удаляет лайк из базы.
     *
     * @param personId - идентификатор пользователя;
     * @param likeType - тип лайка (под постом или комментарием);
     * @param itemId   - идентификатор объекта, которому ставится лайк.
     */
    public void removeLike(long personId, String likeType, long itemId) {
        switch (likeType) {
            case COMMENT_LIKE:
                removeCommentLike(personId, itemId);
                return;
            case POST_LIKE:
                removePostLike(personId, itemId);
        }
    }

    private void putCommentLike(long personId, long commentId) {
        CommentLike commentLike = new CommentLike();
        commentLike.setTime(LocalDateTime.now(TimeUtil.TIME_ZONE));
        commentLike.setPerson(personRepository.findById(personId).orElseThrow());
        commentLike.setComment(commentService.findById(commentId));
        commentLikeRepository.saveAndFlush(commentLike);
    }

    private void removeCommentLike(long personId, long commentId) {
        CommentLike commentLike = commentLikeRepository.findByPersonIdAndCommentId(personId, commentId);
        commentLikeRepository.deleteById(commentLike.getId());
    }

    private void putPostLike(long personId, long postId) {
        PostLike postLike = new PostLike();
        postLike.setTime(LocalDateTime.now(TimeUtil.TIME_ZONE));
        postLike.setPerson(personRepository.findById(personId).orElseThrow());
        postLike.setPost(postService.findById(postId));
        postLikeRepository.saveAndFlush(postLike);
        postService.putLike(postId);
    }

    private void removePostLike(long personId, long postId) {
        PostLike postLike = postLikeRepository.findByPersonIdAndPostId(personId, postId);
        postLikeRepository.deleteById(postLike.getId());
        postService.removeLike(postId);
    }
}
