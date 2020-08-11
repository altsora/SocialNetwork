package sn;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import sn.config.JwtTokenUtil;

public class TokenGenerationTest {

    @Test
    public void generateToken() {

        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();

        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        User user = new User("test@gmail.com", "test", authorities);

        String token = jwtTokenUtil.generateToken(user);
        System.out.println(token);

        Assertions.assertTrue(jwtTokenUtil.validateToken(token, user));
    }
}
