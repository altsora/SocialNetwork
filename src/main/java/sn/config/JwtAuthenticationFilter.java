package sn.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sn.api.response.LoginResponse;
import sn.api.response.PersonResponse;
import sn.api.response.PersonResponseWithToken;
import sn.service.IAccountService;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    @Autowired
    @Qualifier("account-service")
    private IAccountService accountService;


    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, ApplicationContext ctx) {
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl(SecurityConstants.AUTH_LOGIN_URL);
        this.accountService = ctx.getBean(IAccountService.class);
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response) {
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while (true) {
                if ((line = reader.readLine()) == null) {
                    break;
                }
                jb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonObject jsonObject = new JsonParser().parse(jb.toString()).getAsJsonObject();

        var username = jsonObject.get("email").toString().replaceAll("\"", "");
        var password = jsonObject.get("password").toString().replaceAll("\"", "");
        var authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, FilterChain filterChain, Authentication authentication)
        throws UnsupportedEncodingException {

        var user = ((User) authentication.getPrincipal());

        var roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(
            Collectors.toList());

        var signingKey = SecurityConstants.JWT_SECRET.getBytes();

        var token = Jwts.builder()
            .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
            .setHeaderParam("typ", SecurityConstants.TOKEN_TYPE)
            .setIssuer(SecurityConstants.TOKEN_ISSUER).setAudience(SecurityConstants.AUDIENCE)
            .setSubject(user.getUsername())
            .setExpiration(new Date(System.currentTimeMillis() + 864000000)).claim("rol", roles)
            .compact();

        PersonResponse personResponse = null;
        try {
            personResponse = accountService.getPersonResponse(accountService.findById(1));
        } catch (Exception e) {
            e.printStackTrace();
        }

        PersonResponseWithToken personResponseWithToken = new PersonResponseWithToken(personResponse, (SecurityConstants.TOKEN_PREFIX + token));

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setTimestamp(new Date().getTime() / 1000L);
        loginResponse.setData(personResponseWithToken);

        Gson gson = new Gson();
        String loginResponseString = gson.toJson(loginResponse);
        System.out.println(loginResponseString);

        try {
            response.setCharacterEncoding("UTF8");
            response.setContentType("application/json");
            response.getWriter().write(loginResponseString);
            response.getWriter().flush();
            response.getWriter().close();

        } catch (Exception e){
            e.getMessage();
        }
    }
}
