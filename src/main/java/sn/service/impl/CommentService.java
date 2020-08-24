package sn.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sn.api.response.CommentResponse;
import sn.model.Comment;
import sn.repositories.CommentRepository;
import sn.service.ICommentService;
import sn.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс CommentServiceImpl.
 * Сервисный слой для Comment.
 * Имплементирует CommentService.
 *
 * @version 1.0
 * @see ICommentService
 * @see sn.model.Comment
 */


@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {
    private final CommentRepository commentRepository;

    //==================================================================================================================

    /**
     * Поиск комментария по его идентификатору.
     *
     * @param commentId - идентификатор комментария;
     * @return - возвращает комментарий, если существует, иначе null.
     */
    @Override
    public Comment findById(long commentId) {
        return commentRepository.findById(commentId)
                .orElse(null);
    }

    /**
     * Формирует коллекцию CommentResponse.
     *
     * @param postId - идентификатор поста, комментарии которого надо получить;
     * @return - возвращает коллекцию комментарий.
     */
    @Override
    public List<CommentResponse> getCommentsByPostId(long postId) {
        Sort sort = Sort.by(Sort.Direction.ASC, CommentRepository.COMMENT_TIME);
        List<Comment> comments = commentRepository.findAllCommentsByPostId(postId, sort);
        List<CommentResponse> commentResponses = new ArrayList<>();

        for (Comment comment : comments) {
            CommentResponse commentResponse = new CommentResponse();
            commentResponse.setId(comment.getId());
            commentResponse.setPostId(comment.getPost().getId());
            commentResponse.setAuthorId(comment.getAuthor().getId());
            commentResponse.setCommentText(comment.getText());
            commentResponse.setTime(TimeUtil.getTimestampFromLocalDateTime(comment.getTime()));
            commentResponse.setBlocked(comment.isBlocked());
            Comment parent = comment.getParent();
            if (parent != null) {
                commentResponse.setParentId(parent.getParent().getId());
            }

            commentResponses.add(commentResponse);
        }
        return commentResponses;
    }
}
