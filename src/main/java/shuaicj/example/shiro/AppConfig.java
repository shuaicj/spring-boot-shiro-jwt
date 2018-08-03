package shuaicj.example.shiro;

import java.util.List;
import javax.sql.DataSource;

import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shuaicj.example.shiro.security.BCryptPasswordService;
import shuaicj.example.shiro.security.JdbcRealm;
import shuaicj.example.shiro.security.JwtAuthenticationConfig;
import shuaicj.example.shiro.security.JwtTokenAuthFilter;
import shuaicj.example.shiro.security.JwtTokenRealm;
import shuaicj.example.shiro.security.JwtUsernamePasswordAuthFilter;

/**
 * Config beans.
 *
 * @author shuaicj 2018/06/29
 */
@Configuration
public class AppConfig {

    @Bean
    public JwtAuthenticationConfig jwtAuthenticationConfig() {
        return new JwtAuthenticationConfig();
    }

    @Bean
    public JwtUsernamePasswordAuthFilter jwtUsernamePasswordAuthFilter(JwtAuthenticationConfig config,
                                                                       @Qualifier("jdbcRealm") Realm realm) {
        return new JwtUsernamePasswordAuthFilter(config, realm);
    }

    @Bean
    public JwtTokenAuthFilter jwtTokenAuthFilter(JwtAuthenticationConfig config) {
        return new JwtTokenAuthFilter(config);
    }

    @Bean("jdbcRealm")
    public Realm jdbcRealm(DataSource dataSource) {
        PasswordMatcher passwordMatcher = new PasswordMatcher();
        passwordMatcher.setPasswordService(new BCryptPasswordService());
        JdbcRealm jdbcRealm = new JdbcRealm();
        jdbcRealm.setDataSource(dataSource);
        jdbcRealm.setPermissionsLookupEnabled(true);
        jdbcRealm.setCredentialsMatcher(passwordMatcher);
        return jdbcRealm;
    }

    @Bean("jwtTokenRealm")
    public Realm jwtTokenRealm() {
        return new JwtTokenRealm();
    }

    @Bean
    public DefaultWebSecurityManager webSecurityManager(List<Realm> realms) {
        return new DefaultWebSecurityManager(realms);
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition(JwtAuthenticationConfig config) {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();

        // The following two lines indicate that:
        //   1. we need no session, because statelessness is preferred in REST world.
        //   2. use 'jwtUsernamePasswordAuth' to authenticate login request.
        //   3. use 'jwtTokenAuth' to authenticate all other requests.
        //   3. and leave authorization things to annotations in rest controllers.
        chainDefinition.addPathDefinition(config.getUrl(), "noSessionCreation, jwtUsernamePasswordAuth");
        chainDefinition.addPathDefinition("/**", "noSessionCreation, jwtTokenAuth");

        return chainDefinition;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager,
                                                         JwtUsernamePasswordAuthFilter jwtUsernamePasswordAuthFilter,
                                                         JwtTokenAuthFilter jwtTokenAuthFilter,
                                                         ShiroFilterChainDefinition filterChainDefinition) {
        ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();
        filterFactoryBean.setSecurityManager(securityManager);
        filterFactoryBean.getFilters().put("jwtUsernamePasswordAuth", jwtUsernamePasswordAuthFilter);
        filterFactoryBean.getFilters().put("jwtTokenAuth", jwtTokenAuthFilter);
        filterFactoryBean.setFilterChainDefinitionMap(filterChainDefinition.getFilterChainMap());
        return filterFactoryBean;
    }
}
