package sn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sn.model.Person;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    @Qualifier("account-service")
    private IAccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Person person = null;
        UserBuilder builder = null;
        try {
            person = accountService.findByEmail(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (person == null){
                throw new UsernameNotFoundException(username);
            } else {
            builder = User.withUsername(username);
            builder.password(person.getPassword());
            builder.roles("USER");
        }
            return builder.build();
    }
}
