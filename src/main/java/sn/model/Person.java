package sn.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


/**
 * Class Person.
 * Entity Person.
 *
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "person")
@EqualsAndHashCode(exclude = {"posts", "postLikes", "commentLikes", "ownDialogs", "dialogs", "sentMessages", "receivedMessages"})
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @CreationTimestamp
    @Column(name = "reg_date", columnDefinition = "timestamp with time zone")
    private LocalDateTime regDate;
    @Column(name = "birth_date")
    private LocalDate birthDate;
    @Column(name = "e_mail")
    private String email;
    private String phone;
    private String password;
    private String photo;
    private String about;
    private String city;
    private String country;
    @Column(name = "confirmation_code")
    private String confirmationCode;
    @Column(name = "is_approved")
    private boolean approved;
    @Column(name = "messages_permission")
    private String messagesPermission;
    @UpdateTimestamp
    @Column(name = "last_online_time", columnDefinition = "timestamp with time zone")
    private LocalDateTime lastOnlineTime;
    @Column(name = "is_blocked")
    private boolean blocked;
    @Column(name = "is_online")
    private boolean online;
    @Column(name = "is_deleted")
    private boolean deleted;
    @JsonManagedReference
    @OneToMany(mappedBy = "author")
    private List<Post> posts;
    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
    private Set<PostLike> postLikes;
    @JsonManagedReference
    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
    private Set<CommentLike> commentLikes;
    @JsonManagedReference
    @OneToMany(mappedBy = "owner")
    private Set<Dialog> ownDialogs;
    @JsonManagedReference
    @OneToMany(mappedBy = "person")
    private Set<Person2Dialog> dialogs;
    @JsonManagedReference
    @OneToMany(mappedBy = "author")
    private Set<Message> sentMessages;
    @JsonManagedReference
    @OneToMany(mappedBy = "recipient")
    private Set<Message> receivedMessages;

    @Override
    public String toString() {
        return "Person{" +
            "id=" + id +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            '}';
    }
}
