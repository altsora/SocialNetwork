package sn.model;

import java.io.Serializable;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import sn.repositories.PersonRepository;


public class JwtResponse implements Serializable {
    @Autowired
    PersonRepository personRepository;

    private static final long serialVersionUID = -8091879091924046844L;
    private final Person person;
    private final String jwttoken;


    public JwtResponse(String jwttoken, String email) {
        Optional<Person> person = personRepository.findByEmail(email);
        this.person = person.get();
        this.jwttoken = jwttoken;
    }

    public String getToken() {
        return this.jwttoken;
    }
}
