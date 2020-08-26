package sn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sn.api.response.CommentResponse;
import sn.api.response.PersonResponse;
import sn.api.response.WallPostResponse;
import sn.model.Person;
import sn.model.Post;
import sn.model.enums.StatusWallPost;
import sn.repositories.PostRepository;
import sn.service.IPostService;
import sn.utils.TimeUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final PostRepository postRepository;

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
        log.info("User with id {} add new post. Time: {}", author.getId(), postTime);
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

    @Override
    public void putLike(long postId) {
        Post post = findById(postId);
        post.setLikesCount(post.getLikesCount() + 1);
        postRepository.saveAndFlush(post);
    }

    @Override
    public void removeLike(long postId) {
        Post post = findById(postId);
        post.setLikesCount(post.getLikesCount() - 1);
        postRepository.saveAndFlush(post);
    }
}
