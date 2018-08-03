package shuaicj.example.shiro;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import shuaicj.example.shiro.security.JwtAuthenticationConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShiroTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private JwtAuthenticationConfig jwtConfig;

    @Test
    public void testLogin() {
        // good login
        assertThat(loginCode("alice", "alice-password")).isEqualTo(HttpStatus.OK);
        assertThat(loginCode("bob", "bob-password")).isEqualTo(HttpStatus.OK);
        assertThat(loginCode("chris", "chris-password")).isEqualTo(HttpStatus.OK);
        assertThat(loginCode("david", "david-password")).isEqualTo(HttpStatus.OK);
        // bad login
        assertThat(loginCode("alice", "alice-passw")).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(loginCode(null, null)).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void testRoleBased() {
        // alice is an admin
        assertThat(jwtAuth(HttpMethod.GET, "/admin-only", "alice", "alice-password")).isEqualTo(HttpStatus.OK);
        assertThat(jwtAuth(HttpMethod.GET, "/user-only", "alice", "alice-password")).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(jwtAuth(HttpMethod.GET, "/admin-or-user", "alice", "alice-password")).isEqualTo(HttpStatus.OK);
        assertThat(jwtAuth(HttpMethod.GET, "/public-to-all", "alice", "alice-password")).isEqualTo(HttpStatus.OK);
        // bob is a user
        assertThat(jwtAuth(HttpMethod.GET, "/admin-only", "bob", "bob-password")).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(jwtAuth(HttpMethod.GET, "/user-only", "bob", "bob-password")).isEqualTo(HttpStatus.OK);
        assertThat(jwtAuth(HttpMethod.GET, "/admin-or-user", "bob", "bob-password")).isEqualTo(HttpStatus.OK);
        assertThat(jwtAuth(HttpMethod.GET, "/public-to-all", "bob", "bob-password")).isEqualTo(HttpStatus.OK);
        // unauthenticated
        assertThat(jwtAuth(HttpMethod.GET, "/admin-only", null, null)).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(jwtAuth(HttpMethod.GET, "/user-only", null, null)).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(jwtAuth(HttpMethod.GET, "/admin-or-user", null, null)).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(jwtAuth(HttpMethod.GET, "/public-to-all", null, null)).isEqualTo(HttpStatus.OK);
        // bad token
        assertThat(jwtBadTokenAuth(HttpMethod.GET, "/admin-only")).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(jwtBadTokenAuth(HttpMethod.GET, "/user-only")).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(jwtBadTokenAuth(HttpMethod.GET, "/admin-or-user")).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(jwtBadTokenAuth(HttpMethod.GET, "/public-to-all")).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testPermissionBased() {
        // alice is an admin
        assertThat(jwtAuth(HttpMethod.POST, "/read", "alice", "alice-password")).isEqualTo(HttpStatus.OK);
        assertThat(jwtAuth(HttpMethod.POST, "/write", "alice", "alice-password")).isEqualTo(HttpStatus.OK);
        assertThat(jwtAuth(HttpMethod.POST, "/archive", "alice", "alice-password")).isEqualTo(HttpStatus.OK);
        assertThat(jwtAuth(HttpMethod.POST, "/read-log", "alice", "alice-password")).isEqualTo(HttpStatus.OK);
        assertThat(jwtAuth(HttpMethod.POST, "/write-log", "alice", "alice-password")).isEqualTo(HttpStatus.OK);
        assertThat(jwtAuth(HttpMethod.POST, "/archive-log", "alice", "alice-password")).isEqualTo(HttpStatus.OK);
        // bob is a user
        assertThat(jwtAuth(HttpMethod.POST, "/read", "bob", "bob-password")).isEqualTo(HttpStatus.OK);
        assertThat(jwtAuth(HttpMethod.POST, "/write", "bob", "bob-password")).isEqualTo(HttpStatus.OK);
        assertThat(jwtAuth(HttpMethod.POST, "/archive", "bob", "bob-password")).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(jwtAuth(HttpMethod.POST, "/read-log", "bob", "bob-password")).isEqualTo(HttpStatus.OK);
        assertThat(jwtAuth(HttpMethod.POST, "/write-log", "bob", "bob-password")).isEqualTo(HttpStatus.OK);
        assertThat(jwtAuth(HttpMethod.POST, "/archive-log", "bob", "bob-password")).isEqualTo(HttpStatus.FORBIDDEN);
        // chris is a file-operator
        assertThat(jwtAuth(HttpMethod.POST, "/read", "chris", "chris-password")).isEqualTo(HttpStatus.OK);
        assertThat(jwtAuth(HttpMethod.POST, "/write", "chris", "chris-password")).isEqualTo(HttpStatus.OK);
        assertThat(jwtAuth(HttpMethod.POST, "/archive", "chris", "chris-password")).isEqualTo(HttpStatus.OK);
        assertThat(jwtAuth(HttpMethod.POST, "/read-log", "chris", "chris-password")).isEqualTo(HttpStatus.OK);
        assertThat(jwtAuth(HttpMethod.POST, "/write-log", "chris", "chris-password")).isEqualTo(HttpStatus.OK);
        assertThat(jwtAuth(HttpMethod.POST, "/archive-log", "chris", "chris-password")).isEqualTo(HttpStatus.OK);
        // david is a log-archiver
        assertThat(jwtAuth(HttpMethod.POST, "/read", "david", "david-password")).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(jwtAuth(HttpMethod.POST, "/write", "david", "david-password")).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(jwtAuth(HttpMethod.POST, "/archive", "david", "david-password")).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(jwtAuth(HttpMethod.POST, "/read-log", "david", "david-password")).isEqualTo(HttpStatus.OK);
        assertThat(jwtAuth(HttpMethod.POST, "/write-log", "david", "david-password")).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(jwtAuth(HttpMethod.POST, "/archive-log", "david", "david-password")).isEqualTo(HttpStatus.OK);
        // unauthenticated
        assertThat(jwtAuth(HttpMethod.POST, "/read", null, null)).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(jwtAuth(HttpMethod.POST, "/write", null, null)).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(jwtAuth(HttpMethod.POST, "/archive", null, null)).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(jwtAuth(HttpMethod.POST, "/read-log", null, null)).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(jwtAuth(HttpMethod.POST, "/write-log", null, null)).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(jwtAuth(HttpMethod.POST, "/archive-log", null, null)).isEqualTo(HttpStatus.UNAUTHORIZED);
        // bad token
        assertThat(jwtBadTokenAuth(HttpMethod.POST, "/read")).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(jwtBadTokenAuth(HttpMethod.POST, "/write")).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(jwtBadTokenAuth(HttpMethod.POST, "/archive")).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(jwtBadTokenAuth(HttpMethod.POST, "/read-log")).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(jwtBadTokenAuth(HttpMethod.POST, "/write-log")).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(jwtBadTokenAuth(HttpMethod.POST, "/archive-log")).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    ResponseEntity<String> login(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String body = "";
        if (username != null && password != null) {
            body = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        }
        return restTemplate.exchange(jwtConfig.getUrl(), HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
    }

    HttpStatus loginCode(String username, String password) {
        return login(username, password).getStatusCode();
    }

    HttpStatus jwtAuth(HttpMethod method, String url, String tokenHeader) {
        HttpHeaders headers = new HttpHeaders();
        if (tokenHeader != null) {
            headers.add(jwtConfig.getHeader(), tokenHeader);
        }
        ResponseEntity<String> response = restTemplate.exchange(url, method, new HttpEntity<>(headers), String.class);
        return response.getStatusCode();
    }

    HttpStatus jwtAuth(HttpMethod method, String url, String username, String password) {
        List<String> list = login(username, password).getHeaders().get(jwtConfig.getHeader());
        String tokenHeader = list != null ? list.get(0) : null;
        return jwtAuth(method, url, tokenHeader);
    }

    HttpStatus jwtBadTokenAuth(HttpMethod method, String url) {
        String tokenHeader = jwtConfig.getPrefix() + " aabbccdd";
        return jwtAuth(method, url, tokenHeader);
    }
}