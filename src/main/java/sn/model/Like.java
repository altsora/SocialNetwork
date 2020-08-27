package sn.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.model.enums.LikeType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "likes")
@NoArgsConstructor
@Data
public class Like {
    private long id;
    private LocalDateTime time;
    private Person person;
    private LikeType likeType; //POST или COMMENT
    private long itemId;

    //==================================================================================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    @Column(name = "time", nullable = false)
    public LocalDateTime getTime() {
        return time;
    }

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id")
    public Person getPerson() {
        return person;
    }

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    public LikeType getLikeType() {
        return likeType;
    }

    @Column(name = "item_id", nullable = false)
    public long getItemId() {
        return itemId;
    }
}
