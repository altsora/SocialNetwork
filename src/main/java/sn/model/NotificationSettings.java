package sn.model;

import lombok.*;

import javax.persistence.*;

/**
 * @author Andrey.Kazakov
 * @date 04.08.2020
 */

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification_settings")
public class NotificationSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id", nullable = false)
    private Person owner;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "notification_type_id", nullable = false)
    private NotificationType type;
    private boolean enable;
}

