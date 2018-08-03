package shuaicj.example.shiro.security;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.OncePerRequestFilter;

/**
 * Authenticate the request to url /login by POST with json body '{ username, password }'.
 * If successful, response the client with header 'Authorization: Bearer jwt-token'.
 *
 * @author shuaicj 2018/07/27
 */
@Slf4j
public class JwtUsernamePasswordAuthFilter extends OncePerRequestFilter {

    private final JwtAuthenticationConfig config;
    private final JdbcRealm realm;
    private final ObjectMapper mapper;

    public JwtUsernamePasswordAuthFilter(JwtAuthenticationConfig config, Realm realm) {
        this.config = config;
        this.realm = (JdbcRealm) realm;
        this.mapper = new ObjectMapper();
    }

    @Override
    protected boolean isEnabled(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        return req.getMethod().equalsIgnoreCase("POST") && req.getServletPath().equals(config.getUrl());
    }

    @Override
    protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {
        // try authentication
        HttpServletResponse rsp = (HttpServletResponse) response;
        Subject subject = SecurityUtils.getSubject();
        try {
            User u = mapper.readValue(request.getInputStream(), User.class);
            subject.login(new UsernamePasswordToken(u.getUsername(), u.getPassword()));
        } catch (JsonProcessingException | AuthenticationException e) {
            log.error("authentication failed", e);
            rsp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        // generate jwt token
        Instant now = Instant.now();
        AuthorizationInfo info = realm.getAuthorizationInfo(subject);
        String token = Jwts.builder()
                           .setSubject((String) subject.getPrincipal())
                           .claim("roles", info.getRoles())
                           .claim("permissions", info.getStringPermissions())
                           .setIssuedAt(Date.from(now))
                           .setExpiration(Date.from(now.plusSeconds(config.getExpiration())))
                           .signWith(SignatureAlgorithm.HS256, config.getSecret().getBytes())
                           .compact();
        rsp.addHeader(config.getHeader(), config.getPrefix() + " " + token);
    }

    @Getter
    @Setter
    private static class User {
        private String username, password;
    }
}
