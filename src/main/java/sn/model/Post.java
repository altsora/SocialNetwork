package sn.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "posts")
@Data
@EqualsAndHashCode(exclude = {"comments"})
public class Post {
    private long id;
    private LocalDateTime time;
    private Person author;
    private String title;
    private String text;
    private boolean isBlocked;
    private boolean isDeleted;
    private int likesCount;
    private Set<Comment> comments;
    //TODO: будет сет с тегами (Tag2Post)

    //==================================================================================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    @CreationTimestamp
    @Column(name = "time", nullable = false, columnDefinition = "timestamp with time zone")
    public LocalDateTime getTime() {
        return time;
    }

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "author_id")
    public Person getAuthor() {
        return author;
    }

    @Column(name = "title", nullable = false)
    public String getTitle() {
        return title;
    }

    @Column(name = "post_text", nullable = false)
    public String getText() {
        return text;
    }

    @Column(name = "is_blocked")
    public boolean isBlocked() {
        return isBlocked;
    }

    @Column(name = "is_deleted")
    public boolean isDeleted() {
        return isDeleted;
    }

    @Column(name = "likes")
    public int getLikesCount() {
        return likesCount;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<Comment> getComments() {
        return comments;
    }
}
