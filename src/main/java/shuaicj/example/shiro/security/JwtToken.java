package shuaicj.example.shiro.security;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

/**
 * Define the jwt token.
 *
 * @author shuaicj 2018/08/03
 */
@SuppressWarnings("serial")
public class JwtToken implements AuthenticationToken {

    private final String principal;
    private final String token;
    private final Set<String> roles;
    private final Set<String> permissions;

    public JwtToken(String principal, String token, Collection<String> roles, Collection<String> permissions) {
        this.principal = principal;
        this.token = token;
        this.roles = Collections.unmodifiableSet(new HashSet<>(roles));
        this.permissions = Collections.unmodifiableSet(new HashSet<>(permissions));
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    public AuthenticationInfo getAuthenticationInfo(String realmName) {
        return new JwtAuthenticationInfo(realmName);
    }

    public class JwtAuthenticationInfo implements AuthenticationInfo {

        private final PrincipalCollection principals;

        public JwtAuthenticationInfo(String realmName) {
            this.principals = new JwtPrincipalCollection(realmName);
        }

        @Override
        public PrincipalCollection getPrincipals() {
            return principals;
        }

        @Override
        public Object getCredentials() {
            return token;
        }
    }

    public class JwtPrincipalCollection extends SimplePrincipalCollection {

        public JwtPrincipalCollection(String realmName) {
            super(principal, realmName);
        }

        public AuthorizationInfo getAuthorizationInfo() {
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            info.addRoles(roles);
            info.addStringPermissions(permissions);
            return info;
        }
    }
}
