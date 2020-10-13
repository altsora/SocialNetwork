package sn.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sn.api.requests.PostCommentCreateRequest;
import sn.api.requests.PostEditRequest;
import sn.api.response.*;
import sn.model.Comment;
import sn.model.Person;
import sn.model.Post;
import sn.model.enums.LikeType;
import sn.model.enums.StatusWallPost;
import sn.repositories.CommentRepository;
import sn.repositories.PostRepository;
import sn.utils.TimeUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final AccountService accountService;
    private final CommentService commentService;
    private final CommentRepository commentRepository;

    @Lazy
    public PostService(PostRepository postRepository, AccountService accountService,
        CommentService commentService, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.accountService = accountService;
        this.commentService = commentService;
        this.commentRepository = commentRepository;
    }

    /**
     * Поиск поста по его идентификатору.
     *
     * @param postId - идентификатор поста;
     * @return - возвращает пост, если существует, иначе null.
     */
    public Post findById(long postId) {
        return postRepository
            .findById(postId)
            .orElse(null);
    }

    /**
     * Получение записей на стене пользователя.
     *
     * @param personId    - ID пользователя, со стены которого требуется получить записи.
     * @param offset      - Отступ от начала результирующего списка публикаций.
     * @param itemPerPage - Количество публикаций из результирующего списка, которые представлены
     *                    для отображения.
     * @return - получение результирующего списка с публикациями на стене пользователя;.
     */
    public List<Post> findAllByPersonId(long personId, int offset, int itemPerPage) {
        int pageNumber = offset / itemPerPage;
        Sort sort = Sort.by(Sort.Direction.DESC, PostRepository.POST_TIME);
        Pageable pageable = PageRequest.of(pageNumber, itemPerPage, sort);
        return postRepository.findAllByPersonId(personId, pageable);
    }

    /**
     * Добавление новой публикации.
     *
     * @param author   - автор поста;
     * @param title    - заголовок поста;
     * @param text     - текст поста;
     * @param postTime - дата публикации поста;
     * @return - возвращает только что добавленный в базу пост.
     */
    public Post addPost(Person author, String title, String text, LocalDateTime postTime) {
        Post post = new Post();
        post.setTime(postTime);
        post.setAuthor(author);
        post.setTitle(title);
        post.setText(text);
        return postRepository.saveAndFlush(post);
    }

    /**
     * Получить общее количество постов у конкретного пользователя.
     *
     * @param personId - идентификатор пользователя;
     * @return - возвращает общее количество постов у пользователя с указанным идентификатором.
     */
    public int getTotalCountPostsByPersonId(long personId) {
        return postRepository.getTotalCountPostsByPersonId(personId);
    }

    /**
     * Метод формирует WallPostResponse на основе нового поста.
     *
     * @param post   - пост, который был только что добавлен в базу;
     * @param author - автор поста;
     * @return - возвращает WallPost для нового поста.
     */
    public WallPostResponse createNewWallPost(Post post, PersonResponse author) {
        return WallPostResponse.builder()
            .id(post.getId())
            .time(TimeUtil.getTimestampFromLocalDateTime(post.getTime()))
            .author(author)
            .title(post.getTitle())
            .postText(post.getText())
            .isBlocked(post.isBlocked())
            .likesCount(post.getLikesCount())
            .comments(new ArrayList<>())
            .build();
    }

    /**
     * Метод формирует WallPostResponse на основе существующего поста.
     *
     * @param post     - существующий в базе пост;
     * @param author   - автор поста;
     * @param comments - комментарии к посту;
     * @return - возвращает WallPost для существующего поста.
     */
    public WallPostResponse getExistsWallPost(Post post, PersonResponse author,
        List<CommentResponse> comments) {
        return WallPostResponse.builder()
            .id(post.getId())
            .time(TimeUtil.getTimestampFromLocalDateTime(post.getTime()))
            .author(author)
            .title(post.getTitle())
            .postText(post.getText())
            .isBlocked(post.isBlocked())
            .likesCount(post.getLikesCount())
            .comments(comments)
            .statusWallPost(StatusWallPost.POSTED)
            .build();
    }

    /**
     * Метод findCurrentUser. Получение текущего пользователя.
     *
     * @return Person или null, если текущий пользователь не аутентифицирован.
     */
    public Person findCurrentUser() {
        return accountService.findCurrentUser();
    }

    /**
     * Метод findPosts. Поиск публикации.
     *
     * @param text        текст публикации.
     * @param dateFrom    Дата публикации ОТ.
     * @param dateTo      Дата публикации ДО.
     * @param offset      Отступ от начала списка.
     * @param itemPerPage Количество элементов на страницу.
     * @return возвращает список публикации
     */
    public List<PostResponse> findPosts(String text, long dateFrom, long dateTo,
        int offset, int itemPerPage) {
        int pageNumber = offset / itemPerPage;
        Sort sort = Sort.by(Sort.Direction.DESC, PostRepository.POST_TIME);
        Pageable pageable = PageRequest.of(pageNumber, itemPerPage, sort);

        // если время в посте не будем менять на лонг, то оставляем так
        LocalDateTime localDateFrom = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(dateFrom), ZoneId.systemDefault());
        LocalDateTime localDateTo = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(dateTo), ZoneId.systemDefault());
        List<Post> posts = postRepository.findAllByTextAndTime
            (text, localDateFrom, localDateTo, pageable);

        List<PostResponse> response = new ArrayList<>();
        for (Post post : posts) {
            PostResponse postResponse = new PostResponse();
            postResponse.setId(post.getId());

            // если время в посте не будем менять на лонг, то оставляем так:
            ZonedDateTime zdt = ZonedDateTime.of(post.getTime(),
                ZoneId.systemDefault());
            postResponse.setTime(zdt.toInstant().toEpochMilli());

            // С учётом удаления PersonService:
            postResponse.setAuthor
                (accountService.getPersonResponse(post.getAuthor()));

            postResponse.setTitle(post.getTitle());
            postResponse.setPostText(post.getText());
            postResponse.setBlocked(post.isBlocked());
            postResponse.setLikes(post.getLikesCount());
            postResponse.setComments(commentService.getCommentsByPostId(
                post.getId()));
        }
        return response;
    }

    /**
     * Метод findPostById. Поиск публикации.
     *
     * @param id ID публикации.
     * @return возвращает публикацию.
     */
    public PostResponse findPostById(long id) {
        Post post = findById(id);
        PostResponse postResponse = new PostResponse();
        postResponse.setId(post.getId());

        // если время в посте не будем менять на лонг, то оставляем так:
        ZonedDateTime zdt = ZonedDateTime.of(post.getTime(),
            ZoneId.systemDefault());
        postResponse.setTime(zdt.toInstant().toEpochMilli());

        // С учётом удаления PersonService:
        postResponse.setAuthor
            (accountService.getPersonResponse(post.getAuthor()));

        postResponse.setTitle(post.getTitle());
        postResponse.setPostText(post.getText());
        postResponse.setBlocked(post.isBlocked());
        postResponse.setLikes(post.getLikesCount());
        postResponse.setComments(commentService.getCommentsByPostId(
            post.getId()));

        return postResponse;
    }

    /**
     * Метод editPost. Редактирование публикации.
     *
     * @param id          ID публикации.
     * @param publishDate Отложить до определённой даты.
     * @return возвращает публикацию.
     */
    public PostResponse editPost
    (long id, long publishDate, PostEditRequest postEditRequest) {
        Post post = postRepository.getOne(id);

        // если время в посте не будем менять на лонг, то оставляем так
        LocalDateTime localDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(publishDate), ZoneId.systemDefault());
        post.setTime(localDateTime);

        post.setText(postEditRequest.getPostText());
        post.setTitle(postEditRequest.getTitle());
        postRepository.saveAndFlush(post);

        PostResponse postResponse = new PostResponse();
        postResponse.setId(post.getId());

        // если время в посте не будем менять на лонг, то оставляем так:
        ZonedDateTime zdt = ZonedDateTime.of(post.getTime(),
            ZoneId.systemDefault());
        postResponse.setTime(zdt.toInstant().toEpochMilli());

        // С учётом удаления PersonService:
        postResponse.setAuthor
            (accountService.getPersonResponse(post.getAuthor()));

        postResponse.setTitle(post.getTitle());
        postResponse.setPostText(post.getText());
        postResponse.setBlocked(post.isBlocked());
        postResponse.setLikes(post.getLikesCount());
        postResponse.setComments(commentService.getCommentsByPostId(
            post.getId()));

        return postResponse;
    }

    /**
     * Метод deletePost. Удаление публикации. DELETE запрос /api/v1/post/{id}
     *
     * @param id ID публикации.
     * @return id удалённой публикации.
     */
    public IdResponse deletePost(long id) {
        Post post = postRepository.getOne(id);
        post.setDeleted(true);
        postRepository.saveAndFlush(post);
        IdResponse idResponse = new IdResponse();
        idResponse.setId(id);
        return idResponse;
    }

    /**
     * Метод recoverPost. Восстановление публикации.
     *
     * @param id ID публикации.
     * @return возвращает публикацию.
     */
    public PostResponse recoverPost(long id) {
        Post post = postRepository.getOne(id);
        post.setDeleted(false);
        postRepository.saveAndFlush(post);

        PostResponse postResponse = new PostResponse();
        postResponse.setId(post.getId());

        // если время в посте не будем менять на лонг, то оставляем так:
        ZonedDateTime zdt = ZonedDateTime.of(post.getTime(),
            ZoneId.systemDefault());
        postResponse.setTime(zdt.toInstant().toEpochMilli());

        // С учётом удаления PersonService:
        postResponse.setAuthor
            (accountService.getPersonResponse(post.getAuthor()));

        postResponse.setTitle(post.getTitle());
        postResponse.setPostText(post.getText());
        postResponse.setBlocked(post.isBlocked());
        postResponse.setLikes(post.getLikesCount());
        postResponse.setComments(commentService.getCommentsByPostId(
            post.getId()));

        return postResponse;
    }

    /**
     * Метод postComplaint. Подать жалобу на публикацию.
     *
     * @param id ID публикации.
     * @see MessageResponse
     */
    public MessageResponse complaintPost(long id) {
        Post post = findById(id);
        MessageResponse response = new MessageResponse();
        response.setMessage("ok");
        if (post == null) {
            return null;
        } else {
            return response;
        }
    }

    public MessageResponse complaintComment(long id, long commentId) {
        Post post = findById(id);
        MessageResponse response = new MessageResponse();
        response.setMessage("ok");
        if (post == null) {
            return null;
        } else {
            Comment comment = null;
            for (Comment current : post.getComments()) {
                if (current.getId() == commentId) {
                    comment = current;
                    break;
                }
            }
            if (comment == null) {
                return null;
            } else {
                return response;
            }
        }
    }

    /**
     * Метод увеличивет счетчик лайков
     *
     * @param postId - идентификатор поста;
     */
    public void incLikesCount(long postId) {
        Post post = findById(postId);
        post.setLikesCount(post.getLikesCount() + 1);
        postRepository.saveAndFlush(post);
    }

    /**
     * Метод уменьшает счетик лайков.
     *
     * @param postId - идентификатор поста;
     */
    public void decLikesCount(long postId) {
        Post post = findById(postId);
        post.setLikesCount(post.getLikesCount() - 1);
        postRepository.saveAndFlush(post);
    }

    public CommentResponse createPostComment(long id,
        PostCommentCreateRequest postCommentCreateRequest) {

        Comment comment = new Comment();

        if (postRepository.findById(id).isPresent()) {
            Post post = postRepository.findById(id).get();
            comment.setParent(commentService.findById(postCommentCreateRequest.getParentId()));
            comment.setText(postCommentCreateRequest.getCommentText());
            commentRepository.save(comment);
            post.getComments().add(comment);
            return commentToCommentResponse(comment);
        } else {
            return new CommentResponse();
        }
    }

    public CommentResponse editComment(long id, long commentId,
        PostCommentCreateRequest postCommentCreateRequest) {

        if (commentRepository.findById(commentId).isPresent()) {
            Comment comment = commentRepository.findById(commentId).get();
            comment.setText(postCommentCreateRequest.getCommentText());
            commentRepository.saveAndFlush(comment);
            postRepository.findById(id).ifPresent(post -> post.getComments().add(comment));
            return commentToCommentResponse(comment);
        } else {
            return new CommentResponse();
        }
    }

    public IdResponse deleteComment(long id, long commentId) {
        Comment comment = commentService.findById(commentId);
        if (postRepository.findById(id).isPresent()) {
            Post post = postRepository.findById(id).get();
            post.getComments().remove(comment);
        }
        IdResponse response = new IdResponse();
        response.setId(commentId);
        return response;
    }

    public CommentResponse recoverComment(long id, long commentId) {
        if (postRepository.findById(id).isPresent() && commentRepository.findById(commentId).isPresent()) {
            Post post = postRepository.findById(id).get();
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