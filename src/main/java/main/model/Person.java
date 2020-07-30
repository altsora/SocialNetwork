package main.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


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
    private String messages_permission;
    @UpdateTimestamp
    @Column(name = "last_online_time", columnDefinition = "timestamp with time zone")
    private LocalDateTime lastOnlineTime;
    @Column(name = "is_blocked")
    private boolean blocked;
    @Column(name = "is_online")
    private boolean online;
    @Column(name = "is_deleted")
    private boolean deleted;

}
