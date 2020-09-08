package sn.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import sn.api.response.ErrorResponse;
import sn.api.response.LoginResponse;
import sn.api.response.PersonResponse;
import sn.api.response.PersonResponseWithToken;
import sn.repositories.PersonRepository;
import sn.service.IAccountService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.sql.Date;
import java.util.Collections;
import java.util.stream.Collectors;

public class JwtUsernameAndPasswordAuthFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authManager;
    private final JwtConfig jwtConfig;
    private final PersonRepository personRepository;
    private final IAccountService accountService;
    private final String LOGIN_PATH = "/auth/login";

    private ObjectMapper objectMapper = new ObjectMapper();

    public JwtUsernameAndPasswordAuthFilter(AuthenticationManager authManager, JwtConfig jwtConfig,
                                            PersonRepository personRepository,
                                            @Qualifier("account-service") IAccountService accountService) {
        this.authManager = authManager;
        this.jwtConfig = jwtConfig;
        this.personRepository = personRepository;
        this.accountService = accountService;

        // По умолчанию, UsernamePasswordAuthenticationFilter "слушает" путь "/login"
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(LOGIN_PATH, "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {
            // 1. Получаем учетные данные из HTTP-запроса
            UserCredentials creds = new ObjectMapper().readValue(request.getInputStream(), UserCredentials.class);

            // 2. Создаем аутентифицированный объект(с полученными учетными данными) для использования менеджером аутентификации
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    creds.getEmail(), creds.getPassword(), Collections.emptyList());

            // 3. Атентификация пользователя менеджером аутентификации
            return authManager.authenticate(authToken);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
        long now = System.currentTimeMillis();

        @SuppressWarnings("deprecation")
        String token = Jwts.builder()
                .setSubject(authResult.getName())
                // Конвертируем в список строк(важно!)
                .claim("authorities", authResult.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + jwtConfig.getExpiration() * 1000))  // milliseconds!
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecret().getBytes())
                .compact();

        response.addHeader(jwtConfig.getHeader(), jwtConfig.getPrefix() + token);

        enrichAuthenticationResponse(response, true, authResult.getName(), token);
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        enrichAuthenticationResponse(response, false, null, null);
    }

    private void enrichAuthenticationResponse(HttpServletResponse response, boolean success,
                                              String email, String token) {
        try {
            if (success) {
                PersonResponse personResponse =
                        accountService.getPersonResponse(personRepository.findById(1L).orElse(null));

                PersonResponseWithToken personResponseWithToken =
                        new PersonResponseWithToken(personResponse, jwtConfig.getPrefix() + token);

                LoginResponse loginResponse = new LoginResponse();
                loginResponse.setTimestamp(new java.util.Date().getTime() / 1000L);
                loginResponse.setData(personResponseWithToken);

                response.setStatus(HttpServletResponse.SC_OK);
                response.setCharacterEncoding("UTF-8");
                response.getWriter()
                        .println(objectMapper.writeValueAsString(loginResponse));

            }else {
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .error("invalid_request")
                        .errorDescription("string")
                        .build();

                response.getWriter()
                        .println(objectMapper.writeValueAsString(errorResponse));
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
            }

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());

        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Внутренний класс для представления учетных данных пользователя
    @Data
    private static class UserCredentials {
        private String email, password;
    }
}
