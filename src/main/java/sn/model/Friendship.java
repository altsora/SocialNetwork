package sn.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import sn.model.enums.FriendshipStatusCode;

@Data
@Entity
@Table(name = "friendship")
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "src_person_id")
    private Person srcPerson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dst_person_id")
    private Person dstPerson;

    @CreationTimestamp
    @Column(columnDefinition = "timestamp with time zone")
    private LocalDateTime time;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('FRIEND', 'REQUEST', 'BLOCKED', 'DECLINED', 'SUBSCRIBED')")
    private FriendshipStatusCode status;
}
