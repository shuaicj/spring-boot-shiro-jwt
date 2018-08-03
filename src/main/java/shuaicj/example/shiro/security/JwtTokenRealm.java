package shuaicj.example.shiro.security;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import shuaicj.example.shiro.security.JwtToken.JwtPrincipalCollection;

/**
 * The realm for {@link JwtToken}.
 *
 * @author shuaicj 2018/08/03
 */
public class JwtTokenRealm extends AuthorizingRealm {

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
        return ((JwtToken) token).getAuthenticationInfo("jwtTokenRealm");
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return ((JwtPrincipalCollection) principals).getAuthorizationInfo();
    }
}
