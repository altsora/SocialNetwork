package sn.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sn.api.response.CommentResponse;
import sn.model.Comment;
import sn.repositories.CommentRepository;
import sn.service.CommentNotFoundException;
import sn.service.CommentService;
import sn.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс CommentServiceImpl.
 * Сервисный слой для Comment.
 * Имплементирует CommentService.
 *
 * @version 1.0
 * @see sn.service.CommentService
 * @see sn.model.Comment
 */


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    //==================================================================================================================

    @Override
    public Comment findById(long commentId) throws CommentNotFoundException {
        return commentRepository
                .findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not fount by id = " + commentId));
    }

    @Override
    public List<CommentResponse> getCommentsByPostId(long postId) {
        Sort sort = Sort.by(Sort.Direction.ASC, CommentRepository.COMMENT_TIME);
        List<Comment> commentList = commentRepository.findAllCommentsByPostId(postId, sort);
        List<CommentResponse> commentResponseList = new ArrayList<>();

        for (Comment comment : commentList) {
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

            commentResponseList.add(commentResponse);
        }
        return commentResponseList;
    }
}
