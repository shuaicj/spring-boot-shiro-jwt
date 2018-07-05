package shuaicj.example.shiro.realm.file;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Base64;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShiroTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void testRoleBased() {
        // alice is an admin
        assertThat(basicAuth(HttpMethod.GET, "/admin-only", "alice", "alice-password")).isEqualTo(HttpStatus.OK);
        assertThat(basicAuth(HttpMethod.GET, "/user-only", "alice", "alice-password")).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(basicAuth(HttpMethod.GET, "/admin-or-user", "alice", "alice-password")).isEqualTo(HttpStatus.OK);
        assertThat(basicAuth(HttpMethod.GET, "/public-to-all", "alice", "alice-password")).isEqualTo(HttpStatus.OK);
        // bob is a user
        assertThat(basicAuth(HttpMethod.GET, "/admin-only", "bob", "bob-password")).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(basicAuth(HttpMethod.GET, "/user-only", "bob", "bob-password")).isEqualTo(HttpStatus.OK);
        assertThat(basicAuth(HttpMethod.GET, "/admin-or-user", "bob", "bob-password")).isEqualTo(HttpStatus.OK);
        assertThat(basicAuth(HttpMethod.GET, "/public-to-all", "bob", "bob-password")).isEqualTo(HttpStatus.OK);
        // unauthenticated
        assertThat(basicAuth(HttpMethod.GET, "/admin-only", null, null)).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(basicAuth(HttpMethod.GET, "/user-only", null, null)).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(basicAuth(HttpMethod.GET, "/admin-or-user", null, null)).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(basicAuth(HttpMethod.GET, "/public-to-all", null, null)).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testPermissionBased() {
        // alice is an admin
        assertThat(basicAuth(HttpMethod.POST, "/read", "alice", "alice-password")).isEqualTo(HttpStatus.OK);
        assertThat(basicAuth(HttpMethod.POST, "/write", "alice", "alice-password")).isEqualTo(HttpStatus.OK);
        assertThat(basicAuth(HttpMethod.POST, "/archive", "alice", "alice-password")).isEqualTo(HttpStatus.OK);
        assertThat(basicAuth(HttpMethod.POST, "/read-log", "alice", "alice-password")).isEqualTo(HttpStatus.OK);
        assertThat(basicAuth(HttpMethod.POST, "/write-log", "alice", "alice-password")).isEqualTo(HttpStatus.OK);
        assertThat(basicAuth(HttpMethod.POST, "/archive-log", "alice", "alice-password")).isEqualTo(HttpStatus.OK);
        // bob is a user
        assertThat(basicAuth(HttpMethod.POST, "/read", "bob", "bob-password")).isEqualTo(HttpStatus.OK);
        assertThat(basicAuth(HttpMethod.POST, "/write", "bob", "bob-password")).isEqualTo(HttpStatus.OK);
        assertThat(basicAuth(HttpMethod.POST, "/archive", "bob", "bob-password")).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(basicAuth(HttpMethod.POST, "/read-log", "bob", "bob-password")).isEqualTo(HttpStatus.OK);
        assertThat(basicAuth(HttpMethod.POST, "/write-log", "bob", "bob-password")).isEqualTo(HttpStatus.OK);
        assertThat(basicAuth(HttpMethod.POST, "/archive-log", "bob", "bob-password")).isEqualTo(HttpStatus.FORBIDDEN);
        // chris is a file-operator
        assertThat(basicAuth(HttpMethod.POST, "/read", "chris", "chris-password")).isEqualTo(HttpStatus.OK);
        assertThat(basicAuth(HttpMethod.POST, "/write", "chris", "chris-password")).isEqualTo(HttpStatus.OK);
        assertThat(basicAuth(HttpMethod.POST, "/archive", "chris", "chris-password")).isEqualTo(HttpStatus.OK);
        assertThat(basicAuth(HttpMethod.POST, "/read-log", "chris", "chris-password")).isEqualTo(HttpStatus.OK);
        assertThat(basicAuth(HttpMethod.POST, "/write-log", "chris", "chris-password")).isEqualTo(HttpStatus.OK);
        assertThat(basicAuth(HttpMethod.POST, "/archive-log", "chris", "chris-password")).isEqualTo(HttpStatus.OK);
        // david is a log-archiver
        assertThat(basicAuth(HttpMethod.POST, "/read", "david", "david-password")).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(basicAuth(HttpMethod.POST, "/write", "david", "david-password")).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(basicAuth(HttpMethod.POST, "/archive", "david", "david-password")).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(basicAuth(HttpMethod.POST, "/read-log", "david", "david-password")).isEqualTo(HttpStatus.OK);
        assertThat(basicAuth(HttpMethod.POST, "/write-log", "david", "david-password")).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(basicAuth(HttpMethod.POST, "/archive-log", "david", "david-password")).isEqualTo(HttpStatus.OK);
        // unauthenticated
        assertThat(basicAuth(HttpMethod.POST, "/read", null, null)).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(basicAuth(HttpMethod.POST, "/write", null, null)).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(basicAuth(HttpMethod.POST, "/archive", null, null)).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(basicAuth(HttpMethod.POST, "/read-log", null, null)).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(basicAuth(HttpMethod.POST, "/write-log", null, null)).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(basicAuth(HttpMethod.POST, "/archive-log", null, null)).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    HttpStatus basicAuth(HttpMethod method, String url, String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        if (username != null && password != null) {
            String encoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
            headers.add("Authorization", "Basic " + encoded);
        }
        ResponseEntity<String> response = restTemplate.exchange(url, method, new HttpEntity<>(headers), String.class);
        return response.getStatusCode();
    }
}