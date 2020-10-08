package sn.service.impl;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import sn.api.response.LikeCountResponse;
import sn.model.Message;
import sn.model.Person;
import sn.model.enums.LikeType;
import sn.repositories.LikeRepository;
import sn.repositories.PostRepository;
import sn.service.AccountService;
import sn.service.LikeService;
import sn.service.PostService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Класс LikeServiceTest
 * Модульные тесты для LikeService.
 *
 * @see LikeService ;
 */

//        Начальные данные
//        INSERT INTO public.likes (id, item_id, like_type, time, person_id) VALUES (2, 1, 'POST', null, 1);
//        INSERT INTO public.likes (id, item_id, like_type, time, person_id) VALUES (3, 2, 'COMMENT', null, 1);
//        INSERT INTO public.likes (id, item_id, like_type, time, person_id) VALUES (4, 3, 'POST', null, 2);
//        INSERT INTO public.likes (id, item_id, like_type, time, person_id) VALUES (7, 3, 'POST', null, 1);
//        INSERT INTO public.likes (id, item_id, like_type, time, person_id) VALUES (8, 4, 'COMMENT', null, 2);
//        INSERT INTO public.likes (id, item_id, like_type, time, person_id) VALUES (9, 4, 'COMMENT', null, 1);

@RunWith(SpringRunner.class)
@SpringBootTest
public class LikeServiceTest {

    @Autowired
    private LikeService likeService;

    @MockBean
    private PostService postService;

    private final Person person  = new Person();

    /**
     * Проверям метод нахождения лайка
     */

    @Test
    public void likeExistsTest() {
        person.setId(1L);
        //Лайк у поста есть
        Assert.assertTrue(likeService.likeExists(person,1, LikeType.POST));
        //Лайк у коммента есть
        Assert.assertTrue(likeService.likeExists(person,2, LikeType.COMMENT));
        //Лайка нету c нужным id
        Assert.assertFalse(likeService.likeExists(person,-2, LikeType.COMMENT));

        person.setId(-10);
        //Лайка нету у пользователя
        Assert.assertFalse(likeService.likeExists(person,1, LikeType.COMMENT));
        //Лайк есть у другого пользватлея

        person.setId(2);
        Assert.assertTrue(likeService.likeExists(person,3, LikeType.POST));
        //Не тот тип лайка
        Assert.assertFalse(likeService.likeExists(person,3, LikeType.COMMENT));
    }

    /**
     * Проверям список юзеров, которые поставили лайки
     */

    @Test
    public void getUsersOfLikeTest() {
        List<Long> expected = new ArrayList<>();
        expected.add(2L);
        expected.add(1L);
        Assert.assertEquals(expected, likeService.getUsersOfLike(3, LikeType.POST));
        Assert.assertEquals(expected, likeService.getUsersOfLike(4, LikeType.COMMENT));
        Assert.assertEquals(1,likeService.getUsersOfLike(1, LikeType.POST).size());
        //Должен возвращать пустой список
        Assert.assertEquals(0,likeService.getUsersOfLike(-1, LikeType.COMMENT).size());
    }

//      Удаляем лайк, есть добавили в прошлом тесте
//      DELETE FROM likes WHERE items_id = 5;

    /**
     * Проверям ставится ли лайк и не ставится, если он уже поставлен
     */

    @Test
    public void putLikeTest() {
        person.setId(1);
        Mockito.doNothing().when(postService).incLikesCount(1L);
        //Попытка поставить еще один лайк
        Assert.assertFalse(likeService.putLike(person,1, LikeType.POST));
        //Ставим новый лайк
        Assert.assertTrue(likeService.putLike(person,5,LikeType.POST));
        //Проверяем наличие в базе
        Assert.assertTrue(likeService.likeExists(person,5,LikeType.POST));
    }

    /**
     * Проверяем удаление лайка
     */

    @Test
    public void removeLikeTest() {
        person.setId(1);
        Mockito.doNothing().when(postService).decLikesCount(1L);
        //Ставим лайка
        likeService.putLike(person,7,LikeType.COMMENT);
        Assert.assertTrue(likeService.likeExists(person,7,LikeType.COMMENT));
        //Удаляем
        likeService.removeLike(person,7,LikeType.COMMENT);
        //Провеяем, что его больше нет базе
        Assert.assertFalse(likeService.likeExists(person,7, LikeType.COMMENT));
        //Попытка удалить не сущетсвующий лайк
        likeService.removeLike(person,-10, LikeType.COMMENT);
    }


}
