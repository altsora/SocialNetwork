package sn.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import sn.model.CommentLike;
import sn.model.Like;
import sn.model.Person;
import sn.model.PostLike;
import sn.model.enums.LikeType;
import sn.repositories.CommentLikeRepository;
import sn.repositories.LikeRepository;
import sn.repositories.PersonRepository;
import sn.repositories.PostLikeRepository;
import sn.utils.TimeUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LikeService {
    @Autowired
    private LikeRepository likeRepository;

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
        return likeRepository.exists(Example.of(new Like(person, itemId, type)));
    }

    /**
     * Метод список пользователей (их идентификаторы), которые поставили лайк под объектом.
     *
     * @param type   - тип лайка (под постом или комментарием);
     * @param itemId - идентификатор объекта, у которого стоит лайк.
     * @return - возвращает список ID пользователей, поставивших лайк.
     */
    public List<Long> getUsersOfLike(long itemId, LikeType type) {
        List<Long> result = likeRepository.getUsersOfLike(itemId, type);
        return result != null ? result : new ArrayList<>();
    }

    /**
     * Метод создаёт лайк в базе.
     *
     * @param person - пользователь;
     * @param type - тип лайка (под постом или комментарием);
     * @param itemId   - идентификатор объекта, которому ставится лайк.
     */
    public void putLike(Person person, long itemId, LikeType type) {
        likeRepository.save(new Like(person,itemId,type));
    }

    /**
     * Метод удаляет лайк из базы.
     *
     * @param personId - идентификатор пользователя;
     * @param type - тип лайка (под постом или комментарием);
     * @param itemId   - идентификатор объекта, которому ставится лайк.
     */
    public void removeLike(Person person, long itemId, LikeType type) {
        likeRepository.delete(new Like(person,itemId,type));
    }
}
