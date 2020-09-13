package sn.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.context.annotation.RequestScope;
import sn.repositories.PersonRepository;
import sn.service.IAccountService;
import sn.service.JwtUserDetailsService;

import javax.servlet.http.HttpServletResponse;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityTokenConfig extends WebSecurityConfigurerAdapter {

    private final JwtUserDetailsService userDetailsService;
    private final JwtConfig jwtConfig;
    private final PersonRepository personRepository;
    private final IAccountService accountService;

    public SecurityTokenConfig(JwtUserDetailsService userDetailsService, JwtConfig jwtConfig,
                               PersonRepository personRepository, @Qualifier("account-service")  IAccountService accountService) {
        this.userDetailsService = userDetailsService;
        this.jwtConfig = jwtConfig;
        this.personRepository = personRepository;
        this.accountService = accountService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .and()
                .authorizeRequests().antMatchers("/account/register", "/account/password/recovery").permitAll()
                .and()
                .addFilterBefore(new JwtTokenAuthFilter(jwtConfig), JwtUsernameAndPasswordAuthFilter.class)
                .addFilterAfter(new JwtUsernameAndPasswordAuthFilter(authenticationManager(), jwtConfig, personRepository, accountService), JwtTokenAuthFilter.class)
                .authorizeRequests()
                .antMatchers("/api/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .usernameParameter("email")
                .permitAll()
                .and()
                .logout()
                .permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @RequestScope
    public Authentication authentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
