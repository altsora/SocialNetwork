package sn.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.api.requests.PostCommentCreateRequest;
import sn.api.requests.PostEditRequest;
import sn.api.response.*;
import sn.service.CommentService;
import sn.service.PostService;

/**
 * Класс PostController.
 * REST-контроллер для работы с постами.
 *
 * @version 1.0
 */
@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    @Autowired
    public PostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    /**
     * Метод findPosts.
     * Поиск публикации.
     * GET запрос /api/v1/post
     *
     * @param text текст публикации.
     * @param dateFrom Дата публикации ОТ.
     * @param dateTo Дата публикации ДО.
     * @param offset Отступ от начала списка.
     * @param itemPerPage Количество элементов на страницу.
     * @return 200 - успешное получение публикации
     */
    @GetMapping
    public ServiceResponse<AbstractResponse> findPosts(
            @RequestParam String text, @RequestParam long dateFrom,
            @RequestParam long dateTo,@RequestParam int offset,
            @RequestParam int itemPerPage) {
        List<PostResponse> posts = postService.findPosts(
                text, dateFrom, dateTo, offset, itemPerPage);
        PostListResponse postListResponse = new PostListResponse(posts);
        return new ServiceResponse(
                posts.size(), offset, itemPerPage, postListResponse);
    }

    /**
     * Метод findPostById.
     * Поиск публикации.
     * GET запрос /api/v1/post/{id}
     *
     * @param id ID публикации.
     * @return 200 - успешное получение публикации
     */
    @GetMapping("/{id}")
    public ServiceResponse<AbstractResponse> findPostById(
            @PathVariable(value = "id") long id) {
        PostResponse post = postService.findPostById(id);
        ServiceResponse response = new ServiceResponse(post);
        return response;
    }

    /**
     * Метод editPost.
     * Редактирование публикации.
     * PUT запрос /api/v1/post/{id}
     *
     * @param id ID публикации.
     * @param publishDate Отложить до определённой даты.
     * @return 200 - успешное получение публикации
     * @see sn.api.requests.PostEditRequest
     */
    @PutMapping("/{id}")
    public ServiceResponse<AbstractResponse> editPost(
            @PathVariable(value = "id") long id,
            @RequestParam long publishDate,
            @RequestBody PostEditRequest postEditRequest) {
        PostResponse post = postService.editPost
                (id, publishDate, postEditRequest);
        ServiceResponse response = new ServiceResponse(post);
        return response;
    }

    /**
     * Метод deletePost.
     * Удаление публикации.
     * DELETE запрос /api/v1/post/{id}
     *
     * @param id ID публикации.
     * @return 200 - успешное удаление публикации
     * @see IdResponse
     */
    @DeleteMapping("/{id}")
    public ServiceResponse<AbstractResponse> deletePost(
            @PathVariable(value = "id") long id) {
        IdResponse deletePost = postService.deletePost(id);
        ServiceResponse response = new ServiceResponse(deletePost);
        return response;
    }

    /**
     * Метод recoverPost.
     * Восстановление публикации.
     * PUT запрос /api/v1/post/{id}/recover
     *
     * @param id ID публикации.
     * @return 200 - успешное восстановление публикации
     * @see PostResponse
     */
    @PutMapping("/{id}/recover")
    public ServiceResponse<AbstractResponse> recoverPost(
            @PathVariable(value = "id") long id) {
        PostResponse post = postService.recoverPost(id);
        ServiceResponse response = new ServiceResponse(post);
        return response;
    }

    @GetMapping("/{id}/comments")
    public ServiceResponseDataList<CommentResponse> getComments(
        @PathVariable(value = "id") long id,
        @RequestParam int offset,
        @RequestParam int initPerPage) {

        return new ServiceResponseDataList<>(commentService.getCommentsByPostId(id).size(), offset,
            initPerPage, commentService.getCommentsByPostId(id));
    }

    @PostMapping("/{id}/comments")
    public SimpleServiceResponse<CommentResponse> createComment(
        @PathVariable(value = "id") long id, PostCommentCreateRequest postCommentCreateRequest) {

        return new SimpleServiceResponse<>(postService.createPostComment(id,
            postCommentCreateRequest));
    }

    @PutMapping("/{id}/comments/{comment_id}")
    public SimpleServiceResponse<CommentResponse> editComment(
        @PathVariable(value = "id") long id,
        @PathVariable(value = "comment_id") long commentId,
        PostCommentCreateRequest postCommentCreateRequest) {

        return new SimpleServiceResponse<>(postService.editComment(id, commentId,
            postCommentCreateRequest));
    }

    @DeleteMapping("/{id}/comments/{comment_id}")
    public SimpleServiceResponse<IdResponse> deleteComment(
        @PathVariable(value = "id") long id,
        @PathVariable(value = "comment_id") long commentId) {

        return new SimpleServiceResponse<>(postService.deleteComment(id, commentId));
    }

    @PutMapping("/{id}/comments/{comment_id}/recover")
    public SimpleServiceResponse<CommentResponse> recoverComment(
        @PathVariable(value = "id") long id,
        @PathVariable(value = "comment_id") long commentId) {

        return new SimpleServiceResponse<>(postService.recoverComment(id, commentId));
    }

    /**
     * Метод postComplaint.
     * Подать жалобу на публикацию.
     * POST запрос /api/v1/post/{id}/report
     *
     * @param id ID публикации.
     * @return 200 - успешное создание жалобы на публикацию
     */
    @PostMapping("/{id}/report")
    public ServiceResponse<AbstractResponse> postComplaint(
            @PathVariable(value = "id") long id) {
        MessageResponse complaintPost = postService.complaintPost(id);
        ServiceResponse response = new ServiceResponse(complaintPost);
        return response;
    }

    /**
     * Метод commentComplaint.
     * Подать жалобу на комментарий.
     * POST запрос /api/v1/post/{id}/comments/{commentId}/report
     *
     * @param id ID публикации.
     * @param commentId ID комментария.
     * @return 200 - успешное создание жалобы на публикацию
     */
    @PostMapping("/{id}/comments/{commentId}/report")
    public ServiceResponse<AbstractResponse> commentComplaint(
            @PathVariable(value = "id") long id,
            @PathVariable(value = "commentId") long commentId) {
        MessageResponse complaintPost = postService.complaintComment
                (id, commentId);
        ServiceResponse response = new ServiceResponse(complaintPost);
        return response;
    }
}
