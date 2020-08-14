package sn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import sn.model.Person;
import sn.service.impl.PersonService;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private PersonService personService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Person person = null;
        UserBuilder builder = null;
        try {
            person = personService.findByEmail(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (person == null){
                throw new UsernameNotFoundException(username);
            } else {
            builder = User.withUsername(username);
            builder.password(new BCryptPasswordEncoder().encode(person.getPassword()));
            builder.roles("USER");
        }
            return builder.build();
    }
}
