package sn.service.impl;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sn.api.requests.PostCommentCreateRequest;
import sn.api.response.CommentResponse;
import sn.api.response.IdResponse;
import sn.model.Comment;
import sn.model.Post;
import sn.repositories.CommentRepository;
import sn.service.ICommentService;
import sn.service.IPostService;
import sn.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс CommentServiceImpl. Сервисный слой для Comment. Имплементирует CommentService.
 *
 * @version 1.0
 * @see ICommentService
 * @see sn.model.Comment
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {

    private final CommentRepository commentRepository;
    private final IPostService postService;

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

    @Override
    public CommentResponse createPostComment(long id,
        PostCommentCreateRequest postCommentCreateRequest) {

        Comment comment = new Comment();
        Post post = postService.findById(id);
        if (post != null) {
            comment.setParent(findById(postCommentCreateRequest.getParentId()));
            comment.setText(postCommentCreateRequest.getCommentText());
            commentRepository.save(comment);
            post.getComments().add(comment);
            return commentToCommentResponse(comment);
        } else {
            return new CommentResponse();
        }
    }

    @Override
    public CommentResponse editComment(long id, long commentId,
        PostCommentCreateRequest postCommentCreateRequest) {
        Post post = postService.findById(id);
        if (commentRepository.findById(commentId).isPresent()) {
            Comment comment = commentRepository.findById(commentId).get();
            comment.setText(postCommentCreateRequest.getCommentText());
            commentRepository.saveAndFlush(comment);
            post.getComments().add(comment);
            return commentToCommentResponse(comment);
        } else {
            return new CommentResponse();
        }
    }

    @Override
    public IdResponse deleteComment(long id, long commentId) {
        Post post = postService.findById(id);
        Comment comment = findById(commentId);
        if (post != null) {
            post.getComments().remove(comment);
        }

        IdResponse response = new IdResponse();
        response.setId(commentId);
        return response;
    }

    @Override
    public CommentResponse recoverComment(long id, long commentId) {
        Post post = postService.findById(id);
        if (post != null && commentRepository.findById(commentId).isPresent()) {
            Comment comment = commentRepository.findById(commentId).get();
            post.getComments().add(comment);
            return commentToCommentResponse(comment);
        } else {
            return new CommentResponse();
        }
    }

    private CommentResponse commentToCommentResponse(Comment comment) {
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(comment.getId());
        commentResponse.setAuthorId(comment.getAuthor().getId());
        commentResponse.setBlocked(comment.isBlocked());
        commentResponse.setTime(TimeUtil.getTimestampFromLocalDateTime(comment.getTime()));
        commentResponse.setCommentText(comment.getText());
        commentResponse.setPostId(comment.getPost().getId());
        return commentResponse;
    }


}
