package sn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;
//Временное решение
//todo перенести бин в конфиг Security
@Configuration
public class AuthenticationConfig {

    @Bean
    @RequestScope
    public Authentication getActiveUser() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
