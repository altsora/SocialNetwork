package sn.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        try {
            person = personService.findByEmail(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (person == null){
                throw new UsernameNotFoundException(username);
            }
            return new JwtUserPrincipal(person) {
            };
    }

    private class JwtUserPrincipal implements UserDetails {
        private Person person;

        public JwtUserPrincipal(Person person) {
            this.person = person;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            final List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("USER"));
            return authorities;
        }

        @Override
        public String getPassword() {
            return person.getPassword();
        }

        @Override
        public String getUsername() {
            return person.getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return person.isBlocked();
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}
