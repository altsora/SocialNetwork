package sn.service;

import sn.api.response.CommentResponse;
import sn.model.Comment;

import java.util.List;

/**
 * Интерфейс CommentService.
 * Методы для работы с классом Comment.
 *
 * @see sn.model.Comment
 */

public interface CommentService {
    Comment findById(long commentId) throws CommentNotFoundException;
    List<CommentResponse> getCommentsByPostId(long postId);
}
