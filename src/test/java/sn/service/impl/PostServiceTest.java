package sn.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

/**
 * Класс PostServiceTest.
 * Модульные тесты для PostService.
 *
 * @see PostService;
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PostServiceTest {

    @Autowired
    private IPostService postService;

    @MockBean
    private PostRepository postRepository;

    /**
     * Поиск поста. Пост найден в БД
     */
    @Test
    public void findByIdPostExistInDB() {
        Person author = new Person();
        long id = 1;
        String title = "";
        String text = "";
        LocalDateTime postTime = LocalDateTime.now();

        Post post = new Post();
        post.setId(id);
        post.setAuthor(author);
        post.setText(text);
        post.setTitle(title);
        post.setTime(postTime);

        Mockito.doReturn(Optional.of(post)).when(postRepository).findById(id);

        Post postInDB = postService.findById(id);

        Assert.assertSame(postInDB, post);
        Mockito.verify(postRepository, Mockito.times(1)).findById(id);

    }

    /**
     * Поиск поста. Поста нет в БД
     */
    @Test
    public void findByIdPostNotExistInDB() {
        long id = 1;

        Mockito.doReturn(Optional.empty()).when(postRepository).findById(id);

        Post postInDB = postService.findById(id);

        Assert.assertNull(postInDB);
        Mockito.verify(postRepository, Mockito.times(1)).findById(id);

    }

    /**
     * Добавление нового поста.
     */
    @Test
    public void addPost() {
        Post post = new Post();
        Person author = new Person();
        String title = "";
        String text = "";
        LocalDateTime postTime = LocalDateTime.now();

        post.setAuthor(author);
        post.setText(text);
        post.setTitle(title);
        post.setTime(postTime);
        Mockito.doReturn(post).when(postRepository).saveAndFlush(post);
        Post postInDB = postService.addPost(author, title, text, postTime);

        Assert.assertSame(postInDB, post);
        Mockito.verify(postRepository, Mockito.times(1)).saveAndFlush(post);
    }

    /**
     * Получение записей на стене пользователя.
     */
    @Test
    public void findAllByPersonId() {
        int offset = 10;
        int itemPerPage = 10;
        int id = 1;

        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage, Sort.by(Sort.Direction.DESC, PostRepository.POST_TIME));

        List<Post> postList = new ArrayList<>();
        for (int i = 0; i < itemPerPage; i++) {
            postList.add(new Post());
        }

        Mockito.doReturn(postList).when(postRepository).findAllByPersonId(id, pageable);

        List<Post> resultList = postService.findAllByPersonId(id, offset, itemPerPage);

        Assert.assertEquals(resultList, postList);
        Mockito.verify(postRepository, Mockito.times(1)).findAllByPersonId(id, pageable);

    }

    /**
     * Получение общего количества постов у конкретного пользователя.
     */
    @Test
    public void getTotalCountPostsByPersonId() {
        int totalPostCount = 10;
        long id = 1;
        Mockito.doReturn(totalPostCount).when(postRepository).getTotalCountPostsByPersonId(id);

        int totalCount = postService.getTotalCountPostsByPersonId(id);

        Assert.assertEquals(totalCount, totalPostCount);
        Mockito.verify(postRepository, Mockito.times(1)).getTotalCountPostsByPersonId(id);

    }

    /**
     * Формирование WallPostResponse на основе нового поста.
     */
    @Test
    public void createNewWallPost() {
        PersonResponse.PersonResponseBuilder personResponseBuilder = PersonResponse.builder();
        PersonResponse author = personResponseBuilder.build();
        long id = 1;
        String title = "";
        String text = "";
        LocalDateTime postTime = LocalDateTime.now();
        int likeCount = 10;

        Post post = new Post();
        post.setId(id);
        post.setText(text);
        post.setTitle(title);
        post.setTime(postTime);
        post.setLikesCount(likeCount);

        WallPostResponse wallPostResponse = postService.createNewWallPost(post, author);

        Assert.assertEquals(wallPostResponse.getId(), post.getId());
        Assert.assertEquals(wallPostResponse.getTime(), TimeUtil.getTimestampFromLocalDateTime(post.getTime()));
        Assert.assertEquals(wallPostResponse.getAuthor(), author);
        Assert.assertEquals(wallPostResponse.getTitle(), post.getTitle());
        Assert.assertEquals(wallPostResponse.getPostText(), post.getText());
        Assert.assertEquals(wallPostResponse.getLikesCount(), post.getLikesCount());
        Assert.assertEquals(wallPostResponse.isBlocked(), post.isBlocked());
        Assert.assertEquals(wallPostResponse.getComments().size(), 0);
    }

    /**
     * Формирование WallPostResponse на основе существующего поста.
     */
    @Test
    public void getExistsWallPost() {
        PersonResponse.PersonResponseBuilder personResponseBuilder = PersonResponse.builder();
        PersonResponse author = personResponseBuilder.build();
        long id = 1;
        String title = "";
        String text = "";
        LocalDateTime postTime = LocalDateTime.now();
        int likeCount = 10;
        int commentCount = 3;

        Post post = new Post();
        post.setId(id);
        post.setText(text);
        post.setTitle(title);
        post.setTime(postTime);
        post.setLikesCount(likeCount);

        List<CommentResponse> comments = new ArrayList<>();
        for (int i = 0; i < commentCount; i++) {
            comments.add(new CommentResponse());
        }

        WallPostResponse wallPostResponse = postService.getExistsWallPost(post, author, comments);

        Assert.assertEquals(wallPostResponse.getId(), post.getId());
        Assert.assertEquals(wallPostResponse.getTime(), TimeUtil.getTimestampFromLocalDateTime(post.getTime()));
        Assert.assertEquals(wallPostResponse.getAuthor(), author);
        Assert.assertEquals(wallPostResponse.getTitle(), post.getTitle());
        Assert.assertEquals(wallPostResponse.getPostText(), post.getText());
        Assert.assertEquals(wallPostResponse.getLikesCount(), post.getLikesCount());
        Assert.assertEquals(wallPostResponse.isBlocked(), post.isBlocked());
        Assert.assertEquals(wallPostResponse.getComments().size(), commentCount);
        Assert.assertEquals(wallPostResponse.getStatusWallPost(), StatusWallPost.POSTED);
    }
}
