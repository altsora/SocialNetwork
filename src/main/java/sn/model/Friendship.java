package sn.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "friendship")
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private FriendshipStatus friendshipStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "src_person_id")
    private Person srcPerson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dst_person_id")
    private Person dstPerson;
}
