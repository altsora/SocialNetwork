package sn.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "person2dialogs")
public class Person2Dialog {

    private long id;
    private Person person;
    private Dialog dialog;

    //==================================================================================================================

    public Person2Dialog(Person person, Dialog dialog) {
        this.person = person;
        this.dialog = dialog;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id")
    public Person getPerson() {
        return person;
    }

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dialog_id")
    public Dialog getDialog() {
        return dialog;
    }

    @Override
    public String toString() {
        return "Person2Dialog{" +
                "id=" + id +
                '}';
    }
}
