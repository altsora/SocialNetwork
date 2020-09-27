package sn.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import sn.model.enums.FriendshipStatusCode;
import sn.model.enums.LikeType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "person_id")
    private long personId;

    @Column(name = "item_id")
    private long itemId;

    @Enumerated(EnumType.STRING)
    private LikeType likeType;

    @CreationTimestamp
    @Column(columnDefinition = "timestamp with time zone")
    private LocalDateTime time;

    public Like() {}

    public Like(long personId, long itemId, LikeType likeType) {
        this.personId = personId;
        this.itemId = itemId;
        this.likeType = likeType;
    }
}



