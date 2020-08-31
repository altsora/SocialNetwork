package sn.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final AccountService personService;
    private final IPostService postService;
    private final LikeRepository likeRepository;

    @Autowired
    private final PersonRepository personRepository;

    //==================================================================================================================

    @Override
    public boolean likeExists(long personId, LikeType likeType, long itemId) {
        return likeRepository.findLike(personId, likeType, itemId) != null;
    }

    @Override
    public int getCount(LikeType likeType, long itemId) {
        return likeRepository.getCountByTypeAndItemId(likeType, itemId);
    }

    @Override
    public List<Long> getUsersOfLike(LikeType likeType, long itemId) {
        List<Long> users = new ArrayList<>();
        for (Like like : likeRepository.findAllByTypeAndItemId(likeType, itemId)) {
            users.add(like.getPerson().getId());
        }
        return users;
    }

    @Override
    public void putLike(long personId, LikeType likeType, long itemId) {
        Like like = new Like();
        like.setItemId(itemId);
        like.setLikeType(likeType);
        like.setTime(LocalDateTime.now(TimeUtil.TIME_ZONE));
        // TODO Топорно попоравлено, чтобы запускалось, поправить get
        like.setPerson(personRepository.findById(personId).get());
        likeRepository.saveAndFlush(like);
        if (likeType == LikeType.POST) {
            postService.putLike(itemId);
        }
    }

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

    @Override
    public void removeLike(long personId, LikeType likeType, long itemId) {
        Like like = likeRepository.findLike(personId, likeType, itemId);
        likeRepository.deleteById(like.getId());
        if (likeType == LikeType.POST) {
            postService.removeLike(itemId);
        }
    }
}
