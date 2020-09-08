package sn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sn.model.Person;
import sn.repositories.PersonRepository;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private PersonRepository personRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Person person = personRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Username: " + email + " not found"));

        return User.withUsername(email)
                .password(person.getPassword())
                .roles("USER")
                .build();
    }
}
