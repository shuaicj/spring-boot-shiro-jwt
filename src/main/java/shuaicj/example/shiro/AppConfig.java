package shuaicj.example.shiro;

import javax.sql.DataSource;

import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shuaicj.example.shiro.security.BCryptPasswordService;

/**
 * Config beans.
 *
 * @author shuaicj 2018/06/29
 */
@Configuration
public class AppConfig {

    @Bean
    public Realm realm(DataSource dataSource) {
        PasswordMatcher passwordMatcher = new PasswordMatcher();
        passwordMatcher.setPasswordService(new BCryptPasswordService());
        JdbcRealm jdbcRealm = new JdbcRealm();
        jdbcRealm.setDataSource(dataSource);
        jdbcRealm.setPermissionsLookupEnabled(true);
        jdbcRealm.setCredentialsMatcher(passwordMatcher);
        return jdbcRealm;
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();

        // This line indicates that:
        //   1. we need no session, because statelessness is preferred in REST world.
        //   2. allow basic authentication, but NOT require it.
        //   3. and leave authorization things to annotations in rest controllers.
        chainDefinition.addPathDefinition("/**", "noSessionCreation, authcBasic[permissive]");

        return chainDefinition;
    }
}
