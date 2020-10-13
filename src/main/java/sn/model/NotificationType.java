package sn.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import sn.model.enums.NotificationTypeCode;

/**
 * @author Andrey.Kazakov
 * @date 04.08.2020
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "notification_type")
public class NotificationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationTypeCode code;
    private String name;
}
