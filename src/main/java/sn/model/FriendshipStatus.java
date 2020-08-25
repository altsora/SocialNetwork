package sn.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import sn.model.enums.FriendshipStatusCode;

@Data
@Entity
@Table(name = "friendship_status")
public class FriendshipStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @CreationTimestamp
    @Column(columnDefinition = "timestamp with time zone")
    private LocalDateTime time;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum(\"FRIEND\", \"REQUEST\", \"BLOCKED\", \"DECLINED\", \"SUBSCRIBED\")")
    private FriendshipStatusCode code;
}
