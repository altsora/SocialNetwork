package sn.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.api.ResponseDataMessage;
import sn.api.requests.PostEditRequest;
import sn.api.response.*;
import sn.model.Person;
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
     * @return 200 - успешное получение публикации, 400 - bad request,
     * 401 - unauthorized.
     */
    @GetMapping
    public ResponseEntity<ServiceResponse<AbstractResponse>> findPosts(
            @RequestParam String text, @RequestParam long dateFrom,
            @RequestParam long dateTo,@RequestParam int offset,
            @RequestParam int itemPerPage) {
        Person person = postService.findCurrentUser();
        if (person == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ServiceResponse<>("Unauthorized",
                            new ResponseDataMessage("User is not authorized")));
        else {
            List<PostResponse> posts = postService.findPosts(
                    text, dateFrom, dateTo, offset, itemPerPage);
            if (posts.size() == 0)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ServiceResponse<>("Bad request",
                                new ResponseDataMessage("Service unavailable")));
            else {
                PostListResponse postListResponse = new PostListResponse(posts);
                ServiceResponse response = new ServiceResponse(
                        posts.size(), offset, itemPerPage, postListResponse);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        }
    }

    /**
     * Метод findPostById.
     * Поиск публикации.
     * GET запрос /api/v1/post/{id}
     *
     * @param id ID публикации.
     * @return 200 - успешное получение публикации, 400 - bad request,
     * 401 - unauthorized.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse<AbstractResponse>> findPostById(
            @PathVariable(value = "id") long id) {
        Person person = postService.findCurrentUser();
        if (person == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ServiceResponse<>("Unauthorized",
                            new ResponseDataMessage("User is not authorized")));
        else {
            PostResponse post = postService.findPostById(id);
            if (post == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ServiceResponse<>("Bad request",
                                new ResponseDataMessage("Service unavailable")));
            else {
                ServiceResponse response = new ServiceResponse(post);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        }
    }

    /**
     * Метод editPost.
     * Редактирование публикации.
     * PUT запрос /api/v1/post/{id}
     *
     * @param id ID публикации.
     * @param publishDate Отложить до определённой даты.
     * @return 200 - успешное получение публикации, 400 - bad request,
     * 401 - unauthorized.
     * @see sn.api.requests.PostEditRequest
     */
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse<AbstractResponse>> editPost(
            @PathVariable(value = "id") long id,
            @RequestParam long publishDate,
            @RequestBody PostEditRequest postEditRequest) {
        Person person = postService.findCurrentUser();
        if (person == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ServiceResponse<>("Unauthorized",
                            new ResponseDataMessage("User is not authorized")));
        else {
            PostResponse post = postService.editPost
                    (id, publishDate, postEditRequest);
            if (post == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ServiceResponse<>("Bad request",
                                new ResponseDataMessage("Service unavailable")));
            else {
                ServiceResponse response = new ServiceResponse(post);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        }
    }

    /**
     * Метод deletePost.
     * Удаление публикации.
     * DELETE запрос /api/v1/post/{id}
     *
     * @param id ID публикации.
     * @return 200 - успешное удаление публикации, 400 - bad request,
     * 401 - unauthorized.
     * @see IdResponse
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse<AbstractResponse>> deletePost(
            @PathVariable(value = "id") long id) {
        Person person = postService.findCurrentUser();
        if (person == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ServiceResponse<>("Unauthorized",
                            new ResponseDataMessage("User is not authorized")));
        else {
            IdResponse deletePost = postService.deletePost(id);
            if (deletePost == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ServiceResponse<>("Bad request",
                                new ResponseDataMessage("Service unavailable")));
            else {
                ServiceResponse response = new ServiceResponse(deletePost);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        }
    }

    /**
     * Метод recoverPost.
     * Восстановление публикации.
     * PUT запрос /api/v1/post/{id}/recover
     *
     * @param id ID публикации.
     * @return 200 - успешное восстановление публикации, 400 - bad request,
     * 401 - unauthorized.
     * @see PostResponse
     */
    @PutMapping("/{id}/recover")
    public ResponseEntity<ServiceResponse<AbstractResponse>> recoverPost(
            @PathVariable(value = "id") long id) {
        Person person = postService.findCurrentUser();
        if (person == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ServiceResponse<>("Unauthorized",
                            new ResponseDataMessage("User is not authorized")));
        else {
            PostResponse post = postService.recoverPost(id);
            if (post == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ServiceResponse<>("Bad request",
                                new ResponseDataMessage("Service unavailable")));
            else {
                ServiceResponse response = new ServiceResponse(post);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        }
    }

}
