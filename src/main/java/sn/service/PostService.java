package sn.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sn.api.requests.PostEditRequest;
import sn.api.response.*;
import sn.model.Comment;
import sn.model.Person;
import sn.model.Post;
import sn.model.enums.StatusWallPost;
import sn.repositories.PostRepository;
import sn.service.IAccountService;
import sn.service.ICommentService;
import sn.service.IPostService;
import sn.utils.TimeUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final PostRepository postRepository;

    @Autowired
    @Qualifier("account-service")
    private IAccountService accountService;

    @Autowired
    private ICommentService commentService;

    /**
     * Поиск поста по его идентификатору.
     *
     * @param postId - идентификатор поста;
     * @return - возвращает пост, если существует, иначе null.
     */
    @Override
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
     * @param itemPerPage - Количество публикаций из результирующего списка, которые представлены для отображения.
     * @return - получение результирующего списка с публикациями на стене пользователя;.
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
    public WallPostResponse getExistsWallPost(Post post, PersonResponse author, List<CommentResponse> comments) {
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
     * Метод findCurrentUser.
     * Получение текущего пользователя.
     *
     * @return Person или null, если текущий пользователь не аутентифицирован.
     */
    @Override
    public Person findCurrentUser() {
        return accountService.findCurrentUser();
    }

    /**
     * Метод findPosts.
     * Поиск публикации.
     *
     * @param text текст публикации.
     * @param dateFrom Дата публикации ОТ.
     * @param dateTo Дата публикации ДО.
     * @param offset Отступ от начала списка.
     * @param itemPerPage Количество элементов на страницу.
     * @return возвращает список публикации
     */
    @Override
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
     * Метод findPostById.
     * Поиск публикации.
     *
     * @param id ID публикации.
     * @return возвращает публикацию.
     */
    @Override
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
     * Метод editPost.
     * Редактирование публикации.
     *
     * @param id ID публикации.
     * @param publishDate Отложить до определённой даты.
     * @return возвращает публикацию.
     */
    @Override
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
     * Метод deletePost.
     * Удаление публикации.
     * DELETE запрос /api/v1/post/{id}
     *
     * @param id ID публикации.
     * @return id удалённой публикации.
     */
    @Override
    public IdResponse deletePost(long id) {
        Post post = postRepository.getOne(id);
        post.setDeleted(true);
        postRepository.saveAndFlush(post);
        IdResponse idResponse = new IdResponse();
        idResponse.setId(id);
        return idResponse;
    }

    /**
     * Метод recoverPost.
     * Восстановление публикации.
     *
     * @param id ID публикации.
     * @return возвращает публикацию.
     */
    @Override
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
     * Метод postComplaint.
     * Подать жалобу на публикацию.
     *
     * @param id ID публикации.
     * @see MessageResponse
     */
    @Override
    public MessageResponse complaintPost(long id) {
        Post post = findById(id);
        MessageResponse response = new MessageResponse();
        response.setMessage("ok");
        if (post == null)
            return null;
        else
            return response;
    }

    @Override
    public MessageResponse complaintComment(long id, long commentId) {
        Post post = findById(id);
        MessageResponse response = new MessageResponse();
        response.setMessage("ok");
        if (post == null)
            return null;
        else {
            Comment comment = null;
            for (Comment current : post.getComments())
                if (current.getId() == commentId) {
                    comment = current;
                    break;
                }
            if (comment == null)
                return null;
            else
                return response;
        }
    }
}