package sn.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sn.model.Like;
import sn.model.enums.LikeType;
import sn.repositories.LikeRepository;
import sn.repositories.PersonRepository;
import sn.service.ILikeService;
import sn.service.IPostService;
import sn.utils.TimeUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService implements ILikeService {
    private final PersonRepository personRepository;
    private final IPostService postService;
    private final LikeRepository likeRepository;

    //==================================================================================================================

    /**
     * Проверка, существует ли лайк (под постом или комментарием).
     *
     * @param personId - идентификатор пользователя;
     * @param likeType - тип лайка (под постом или комментарием);
     * @param itemId   - идентификатор объекта, у которого проверяется наличие лайка.
     * @return - возвращает true, если лайк стоит, иначе false.
     */
    @Override
    public boolean likeExists(long personId, LikeType likeType, long itemId) {
        return likeRepository.findLike(personId, likeType, itemId) != null;
    }

    /**
     * Метод возвращает количество лайков под объектом.
     *
     * @param likeType - тип лайка (под постом или комментарием);
     * @param itemId   - идентификатор объекта, у которого проверяется количество лайков.
     * @return - возвращает количество лайков у объекта.
     */
    @Override
    public int getCount(LikeType likeType, long itemId) {
        return likeRepository.getCountByTypeAndItemId(likeType, itemId);
    }

    /**
     * Метод список пользователей (их идентификаторы), которые поставили лайк под объектом.
     *
     * @param likeType - тип лайка (под постом или комментарием);
     * @param itemId   - идентификатор объекта, у которого стоит лайк.
     * @return - возвращает список ID пользователей, поставивших лайк.
     */
    @Override
    public List<Long> getUsersOfLike(LikeType likeType, long itemId) {
        List<Long> users = new ArrayList<>();
        for (Like like : likeRepository.findAllByTypeAndItemId(likeType, itemId)) {
            users.add(like.getPerson().getId());
        }
        return users;
    }

    /**
     * Метод создаёт лайк в базе.
     *
     * @param likeType - тип лайка (под постом или комментарием);
     * @param itemId   - идентификатор объекта, которому ставится лайк.
     */
    @Override
    public void putLike(long personId, LikeType likeType, long itemId) {
        Like like = new Like();
        like.setItemId(itemId);
        like.setLikeType(likeType);
        like.setTime(LocalDateTime.now(TimeUtil.TIME_ZONE));
        like.setPerson(personRepository.findById(personId).orElseThrow());
        likeRepository.saveAndFlush(like);
        if (likeType == LikeType.POST) {
            postService.putLike(itemId);
        }
    }

    /**
     * Метод возвращает тип лайка на основе текстового описания типа объекта.
     *
     * @param type - текстовое описание типа лайка;
     * @return - тип лайка.
     */
    @Override
    public LikeType getLikeType(String type) {
        switch (type) {
            case "Post":
                return LikeType.POST;
            case "Comment":
                return LikeType.COMMENT;
            default:
                return null;
        }
    }

    /**
     * Метод удаляет лайк из базы.
     *
     * @param likeType - тип лайка (под постом или комментарием);
     * @param itemId   - идентификатор объекта, которому ставится лайк.
     */
    @Override
    public void removeLike(long personId, LikeType likeType, long itemId) {
        Like like = likeRepository.findLike(personId, likeType, itemId);
        likeRepository.deleteById(like.getId());
        if (likeType == LikeType.POST) {
            postService.removeLike(itemId);
        }
    }
}
