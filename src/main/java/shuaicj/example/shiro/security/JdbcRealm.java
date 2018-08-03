package shuaicj.example.shiro.security;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;

/**
 * This JdbcRealm provides the ability to get authorization info of a subject. The access privilege of
 * {@link #getAuthorizationInfo(Subject)} in this class is 'package-private', while it is 'protected' in
 * {@link org.apache.shiro.realm.jdbc.JdbcRealm#getAuthorizationInfo(PrincipalCollection)}, so that
 * {@link JwtUsernamePasswordAuthFilter} can access this method.
 *
 * @author shuaicj 2018/08/03
 */
public class JdbcRealm extends org.apache.shiro.realm.jdbc.JdbcRealm {

    AuthorizationInfo getAuthorizationInfo(Subject subject) {
        return doGetAuthorizationInfo(subject.getPrincipals());
    }
}
