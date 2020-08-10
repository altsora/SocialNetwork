package sn.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Andrey.Kazakov
 * @date 04.08.2020
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id",
            nullable = false)
    private NotificationType type;
    @CreationTimestamp
    @Column(name = "sent_time",
            columnDefinition = "timestamp with time zone")
    private LocalDateTime sentTime;
    @Column(name = "entity_id",
            nullable = false)
    private long entityId;
    private String info;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id", nullable = false)
    private Person toWhom;
    private String contact;
    @Column(name = "is_readed",
            nullable = false)
    private boolean isReaded;
}
