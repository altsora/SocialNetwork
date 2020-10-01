package sn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sn.model.Like;
import sn.model.Person;
import sn.model.enums.LikeType;
import sn.repositories.LikeRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostService postService;
    //==================================================================================================================

    /**
     * Проверка, существует ли лайк (под постом или комментарием).
     *
     * @param person   - пользователь;
     * @param type     - тип лайка (под постом или комментарием);
     * @param itemId   - идентификатор объекта, у которого проверяется наличие лайка.
     * @return - возвращает true, если лайк стоит, иначе false.
     */
    public boolean likeExists(Person person, long itemId, LikeType type) {
        return likeRepository.likeExist(person.getId(), itemId, type.toString())>0;
    }

    /**
     * Метод список пользователей (их идентификаторы), которые поставили лайк под объектом.
     *
     * @param type   - тип лайка (под постом или комментарием);
     * @param itemId - идентификатор объекта, у которого стоит лайк.
     * @return - возвращает список ID пользователей, поставивших лайк.
     */
    public List<Long> getUsersOfLike(long itemId, LikeType type) {
        List<Long> result = likeRepository.getUsersOfLike(itemId, type.toString());
        return result != null ? result : new ArrayList<>();
    }

    /**
     * Метод создаёт лайк в базе.
     *
     * @param person - пользователь;
     * @param type - тип лайка (под постом или комментарием);
     * @param itemId   - идентификатор объекта, которому ставится лайк.
     */
    public boolean putLike(Person person, long itemId, LikeType type) {
        if (likeExists(person,itemId,type)) {
            return false;
        }
        likeRepository.save(new Like(person,itemId,type));
        if (type == LikeType.POST) {
            postService.incLikesCount(itemId);
        }
        return true;
    }

    /**
     * Метод удаляет лайк из базы.
     *
     * @param person - пользователь;
     * @param type - тип лайка (под постом или комментарием);
     * @param itemId   - идентификатор объекта, которому ставится лайк.
     */
    public void removeLike(Person person, long itemId, LikeType type) {
        Long deleteId = likeRepository.findLikeId(person.getId(),itemId,type.toString());
        if (deleteId!=null) {
            likeRepository.deleteById(deleteId);
            if (type == LikeType.POST) {
                postService.decLikesCount(itemId);
            }
        }
    }
}
