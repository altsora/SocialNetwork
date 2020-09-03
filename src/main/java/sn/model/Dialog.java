package sn.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "dialogs")
@NoArgsConstructor
@Data
@EqualsAndHashCode(exclude = {"messages", "persons"})
public class Dialog {
    private long id;
    private Person owner;
    private int unreadCount;
    private boolean isDeleted;
    private String inviteCode;
    private Set<Message> messages;
    private Set<Person2Dialog> persons;

    //==================================================================================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id")
    public Person getOwner() {
        return owner;
    }

    @Column(name = "unread_count", nullable = false)
    public int getUnreadCount() {
        return unreadCount;
    }

    @Column(name = "is_deleted", nullable = false)
    public boolean isDeleted() {
        return isDeleted;
    }

    @Column(name = "invite_code")
    public String getInviteCode() {
        return inviteCode;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "dialog")
    public Set<Message> getMessages() {
        return messages;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "dialog", fetch = FetchType.LAZY)
    public Set<Person2Dialog> getPersons() {
        return persons;
    }
}
