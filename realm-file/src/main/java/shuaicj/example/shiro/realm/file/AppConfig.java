package shuaicj.example.shiro.realm.file;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.PropertiesRealm;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Config beans.
 *
 * @author shuaicj 2018/06/29
 */
@Configuration
public class AppConfig {

    @Bean
    public Realm realm() {
        return new PropertiesRealm(); // classpath:shiro-users.properties
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        // permit all by default, and authorize by annotations in controllers
        chainDefinition.addPathDefinition("/**", "authcBasic[permissive]");
        return chainDefinition;
    }
}
