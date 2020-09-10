package sn.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.api.requests.PostEditRequest;
import sn.api.response.*;
import sn.service.IPostService;

import java.util.List;

/**
 * Класс PostController.
 * REST-контроллер для работы с постами.
 *
 * @version 1.0
 */
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final IPostService postService;

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
    public ResponseEntity<ServiceResponse<AbstractResponse>> findPosts(
            @RequestParam String text, @RequestParam long dateFrom,
            @RequestParam long dateTo,@RequestParam int offset,
            @RequestParam int itemPerPage) {
        List<PostResponse> posts = postService.findPosts(
                text, dateFrom, dateTo, offset, itemPerPage);
        PostListResponse postListResponse = new PostListResponse(posts);
        ServiceResponse response = new ServiceResponse(
                posts.size(), offset, itemPerPage, postListResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
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
    public ResponseEntity<ServiceResponse<AbstractResponse>> findPostById(
            @PathVariable(value = "id") long id) {
        PostResponse post = postService.findPostById(id);
        ServiceResponse response = new ServiceResponse(post);
        return ResponseEntity.status(HttpStatus.OK).body(response);
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
    public ResponseEntity<ServiceResponse<AbstractResponse>> editPost(
            @PathVariable(value = "id") long id,
            @RequestParam long publishDate,
            @RequestBody PostEditRequest postEditRequest) {
        PostResponse post = postService.editPost
                (id, publishDate, postEditRequest);
        ServiceResponse response = new ServiceResponse(post);
        return ResponseEntity.status(HttpStatus.OK).body(response);
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
    public ResponseEntity<ServiceResponse<AbstractResponse>> deletePost(
            @PathVariable(value = "id") long id) {
        IdResponse deletePost = postService.deletePost(id);
        ServiceResponse response = new ServiceResponse(deletePost);
        return ResponseEntity.status(HttpStatus.OK).body(response);
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
    public ResponseEntity<ServiceResponse<AbstractResponse>> recoverPost(
            @PathVariable(value = "id") long id) {
        PostResponse post = postService.recoverPost(id);
        ServiceResponse response = new ServiceResponse(post);
        return ResponseEntity.status(HttpStatus.OK).body(response);
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
    public ResponseEntity<ServiceResponse<AbstractResponse>> postComplaint(
            @PathVariable(value = "id") long id) {
        MessageResponse complaintPost = postService.complaintPost(id);
        ServiceResponse response = new ServiceResponse(complaintPost);
        return ResponseEntity.status(HttpStatus.OK).body(response);
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
    public ResponseEntity<ServiceResponse<AbstractResponse>> commentComplaint(
            @PathVariable(value = "id") long id,
            @PathVariable(value = "commentId") long commentId) {
        MessageResponse complaintPost = postService.complaintComment
                (id, commentId);
        ServiceResponse response = new ServiceResponse(complaintPost);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
