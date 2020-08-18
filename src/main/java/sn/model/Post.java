package sn.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Evgeniia.Kochetkova
 * @date 12.08.2020
 * @version 1.0
 */

@Data
@NoArgsConstructor
@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @CreationTimestamp
    @Column(columnDefinition = "timestamp with time zone")
    private LocalDateTime time;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private Person author;

    @Column(nullable = false)
    private String title;

    @Column(name = "post_text", nullable = false)
    private String text;

    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}
